<script type="text/javascript" src="/salesresources/js/popup_extend_grid.js"></script>
<script type="text/javascript" src="/crmresources/js/miscUtil.js"></script>
<script type="text/javascript">
    var configDataSync = {};
    var olbiusAreaChartObj;
    var olbiusPieChartRegionObj;

    <#assign distributorId = userLogin.partyId />
    <#assign categoryList = delegator.findByAnd("ProductCategory", Static["org.ofbiz.base.util.UtilMisc"].toMap("productCategoryTypeId", "CATALOG_CATEGORY"), null, false)!>
    <!--<#assign agency = delegator.findList("PartyCustomer", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("distributorId", distributorId)), null, null, null, false)>
    -->
    <#assign agency = delegator.findList("PartyCustomer", null, null, null, null, false)>
    var productStoreData3 = [{'text': '${StringUtil.wrapString(uiLabelMap.BSAllObject)}', 'value': 'all'}];
    var dateCurrent = new Date();
    var currentFirstDay = new Date(dateCurrent.getFullYear(), dateCurrent.getMonth(), 1);

	var categoryData = [
	    <#list categoryList as categoryL>
	    {
	    	categoryId : "${categoryL.productCategoryId}",
	    	categoryName: "${StringUtil.wrapString(categoryL.get("categoryName", locale))}"
	    },
	    </#list>
	];

	var listOrderStatusSource = [
		{'text': '${StringUtil.wrapString(uiLabelMap.BSMCreated)}', 'value': 'ORDER_CREATED'},
		{'text': '${StringUtil.wrapString(uiLabelMap.BSMApproved)}', 'value': 'ORDER_APPROVED'},
		{'text': '${StringUtil.wrapString(uiLabelMap.BSMCompleted)}', 'value': 'ORDER_COMPLETED'},
	];

	var agencyData = [
		<#if agency?exists>
			<#list agency as itemAgency>
				{
					partyId: "${itemAgency.partyId?if_exists}",
					groupName: "${StringUtil.wrapString(itemAgency.get("fullName", locale)?if_exists)}"
				},
			</#list>
		</#if>
	];
</script>

<div id="olbiusTorProductStore"></div>
<script type="text/javascript">
    $(function() {
        var config = {
            title: "${StringUtil.wrapString(uiLabelMap.BSTurnoverReport)}",
            service: "salesOrder",
            button: true,
            id: "olbiusTorProductStore",
            olap: "evaluateTurnoverDistributorGrid",
            sortable: true,
            filterable: true,
            showfilterrow: true,
	        columns: [
	            { text: '${uiLabelMap.BSNo2}', sortable: false, filterable: false, editable: false, pinned: true, groupable: false, draggable: false, resizable: false,
			        datafield: 'stt', columntype: 'number', width: '3%',
			        cellsrenderer: function (row, column, value) {
			    	    return '<div style=margin:4px;>' + (value + 1) + '</div>';
			    	}
			    },
	            { text: '${StringUtil.wrapString(uiLabelMap.BSMAgency)}', datafield: {name: "agencyId", type: "string"}, width: '12%'},
	            { text: '${StringUtil.wrapString(uiLabelMap.BSAgentName)}', datafield: {name:'agencyName', type: 'string'}, width: '15%'},
	            { text: '${StringUtil.wrapString(uiLabelMap.BSCategory)}', datafield: {name:'category', type: 'string'}, width: '15%'},
	            { text: '${StringUtil.wrapString(uiLabelMap.BSPercent)}', datafield: {name:'percent', type: 'string'}, width: '10%', hidden: true},
	            { text: '${StringUtil.wrapString(uiLabelMap.BSProductId)}', datafield: {name:'productId', type: 'string'}, width: '10%'},
	            { text: '${StringUtil.wrapString(uiLabelMap.BSProductName)}', datafield: {name:'productName', type: 'string'}, width: '30%'},
	            { text: '${StringUtil.wrapString(uiLabelMap.BSQuantity)}', datafield: {name:'quantity1', type: 'number'}, width: '7%', cellsformat: 'n2', cellsalign: 'right', filterable: false},
	            { text: '${StringUtil.wrapString(uiLabelMap.BSUom)}', datafield: {name:'unit', type: 'string'}, width: '7%', filterable: false},
	            { text: '${StringUtil.wrapString(uiLabelMap.BSValueTotal)}', datafield: {name:'total1', type: 'number'}, width: '11%', cellsformat: 'f0', cellsalign: 'right', filterable: false}
	        ],
	        popup: [
                {
                    group: "dateTime",
                    id: "dateTime",
                    params: {
                    	index: 4,
                    }
                },
                {
                    action: 'jqxDropDownList',
                    params: {
                        id: 'productStore',
                        label: "${StringUtil.wrapString(uiLabelMap.BSSalesChannel)}",
                        source: productStoreData3,
                        selectedIndex: 0
                    },
                    hide: true
                },
                {
                    action: 'jqxGridMultiple',
                    params: {
                    	id : 'category',
        	            label : '${StringUtil.wrapString(uiLabelMap.BSCategoryId)}',
        	            grid: {
        	            	source: categoryData,
        	            	id: "categoryId",
        	            	width: 500,
        	            	sortable: true,
        	                pagesize: 5,
        	                columnsresize: true,
        	                pageable: true,
        	                altrows: true,
        	                showfilterrow: true,
        	                filterable: true,
        	            	columns: [
	            	          	{ text: "${StringUtil.wrapString(uiLabelMap.BSCategoryId)}", datafield: 'categoryId', width: 150 },
	            	          	{ text: "${StringUtil.wrapString(uiLabelMap.BSCategoryName)}", datafield: 'categoryName' }
	            	        ]
        	            }
                    }
                },
                {
                    action: 'jqxDropDownList',
                    params: {
                        id: 'orderStatus',
                        label: "${StringUtil.wrapString(uiLabelMap.BSOrderStatus)}",
                        source: listOrderStatusSource,
                        selectedIndex: _.find(_.map(listOrderStatusSource, function(obj, key){
					    	if (obj.value == "ORDER_COMPLETED") {
								return key;
							}
					    }), function(key){ return key })
                    }
                },
                {
                    action: 'jqxGridMultiple',
                    params: {
                    	id : 'agency',
        	            label : '${StringUtil.wrapString(uiLabelMap.BSMAgency)}',
        	            grid: {
        	            	source: agencyData,
        	            	id: "partyId",
        	            	width: 500,
        	            	sortable: true,
        	                pagesize: 5,
        	                columnsresize: true,
        	                pageable: true,
        	                altrows: true,
        	                showfilterrow: true,
        	                filterable: true,
        	            	columns: [
	            	          	{ text: "${StringUtil.wrapString(uiLabelMap.BSMAgency)}", datafield: 'partyId', width: 100 },
	            	          	{ text: "${StringUtil.wrapString(uiLabelMap.BSMAgencyName)}", datafield: 'groupName' }
	            	        ]
        	            }
                    }
                }
	        ],
            apply: function (grid, popup) {
                var dateTimeData = popup.group("dateTime").val();
                var popupData = $.extend(dateTimeData, {})
                configDataSync.fromDate = popupData.fromDate;
                configDataSync.thruDate = popupData.thruDate;
                configDataSync.dateType = popupData.dateType;
                configDataSync.productStore = getValuePopupItem(popup.val("productStore"));
                configDataSync.category = getValuePopupItem(popup.val('category'));
                configDataSync.orderStatus = OlbCore.isNotEmpty(popup.val('orderStatus'))?[popup.val('orderStatus')]:[]
                configDataSync.agency = OlbCore.isNotEmpty(popup.val("agency")) && popup.val("agency").length != agencyData.length?popup.val("agency"):[];
                if (olbiusAreaChartObj) olbiusAreaChartObj.update();
                if (olbiusPieChartRegionObj) olbiusPieChartRegionObj.update();
                return $.extend({
					productStore: getValuePopupItem(popup.val("productStore")),
					category: getValuePopupItem(popup.val('category')),
					orderStatus: OlbCore.isNotEmpty(popup.val('orderStatus'))?[popup.val('orderStatus')]:[],
					agency: OlbCore.isNotEmpty(popup.val("agency")) && popup.val("agency").length != agencyData.length?popup.val("agency"):[],
				}, popup.group("dateTime").val());
            }
        };

        var grid = OlbiusUtil.grid(config);

        $('body').on("runolapservicedone", function(){
        	if (olbiusAreaChartObj) olbiusAreaChartObj.update();
        	if (olbiusPieChartRegionObj) olbiusPieChartRegionObj.update();
        });
    });

    function getValuePopupItem(value){
        return OlbCore.isNotEmpty(value)?value:["all"];
    }
</script>

<#-- ======================================== AREA CHART =========================================== -->
<div id="olbiusAreaChartTorProduct" class="margin-top30 container-chart-inner-page"></div>
<script type="text/javascript">
	$(function(){
		var config = {
			service: "salesOrder",
            id: "olbiusAreaChartTorProduct",
            olap: "evaluateTurnoverDistributorChart",
            chart: {
                type: 'column'
            },
            title: {
                text: '${StringUtil.wrapString(uiLabelMap.BSPPSColumn)}',
                x: -20 //center
            },
            xAxis: {
                type: 'category',
                labels: {
                    rotation: -30,
                    style: {
                        fontSize: '13px',
                        fontFamily: 'Verdana, sans-serif'
                    }
                },
                title : {
                    text: null
                }
            },
            yAxis: {
                plotLines: [{
                    value: 0,
                    width: 1,
                    color: '#000000'
                }],
                title : {
                    text: null
                },
                min: 0
            },
            legend: {
                enabled: true
            },
            tooltip: {
                pointFormat: '<span style="color:{series.color}">{series.name}</span>: <b>{point.y} VND</b><br/>',
            },
            plotOptions: {
	            series: {
	                maxPointWidth: 30
	            }
	        },
	        height: 0.35,
            chartRender : OlbiusUtil.getChartRender('defaultColumnFunc'),

            apply: function (grid, popup) {
                return $.extend({
                	olapType: 'COLUMNCHART',
                }, configDataSync);
            }
        };

		olbiusAreaChartObj = OlbiusUtil.chart(config);
	});
</script>
<#-- ================================================= PIE CHART ======================================== -->
<div id="olbiusPieChartRegion" class="margin-top30 container-chart-inner-page"></div>
<script type="text/javascript">
	$(function(){
		var configPieChartRegion = {
			service: "salesOrder",
            id: "olbiusPieChartRegion",
            olap: "evaluateTurnoverDistributorChartPie",

            chart: {
                plotBackgroundColor: null,
                plotBorderWidth: null,
                plotShadow: false
            },
            title: {
                text: '${StringUtil.wrapString(uiLabelMap.BSTurnOverChartBySalesChannel)}'
            },
            tooltip: {
                pointFormat: '<b>{point.percentage:.1f}%: {point.y} VND</b>'
            },
            series: [{
                type: 'pie'
            }],
            plotOptions: {
                pie: {
                    allowPointSelect: true,
                    cursor: 'pointer',
                    dataLabels: {
                        enabled: false,
                        format: '<b>{point.name}</b>: {point.percentage:.1f} %',
                        style: {
                            color: (Highcharts.theme && Highcharts.theme.contrastTextColor) || 'black'
                        }
                    },
                    showInLegend: true
                }
            },

            apply: function (grid, popup) {
                return $.extend({
                	olapType: 'PIECHART'
                }, configDataSync);
            },
            height: 0.6,
            chartRender : OlbiusUtil.getChartRender('defaultPieFunc')
        };

		olbiusPieChartRegionObj = OlbiusUtil.chart(configPieChartRegion);
	});
</script>
