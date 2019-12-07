<style>
	.olbiusChartContainer, .olbiusGridContainer{
		margin-bottom: 50px!important;
	}
</style>
<script type="text/javascript" src="/salesresources/js/popup.extend.js"></script>
<script type="text/javascript">
	var dateCurrent = new Date();
	var currentFirstDay = new Date(dateCurrent.getFullYear(), dateCurrent.getMonth(), 1);	

	<#assign salesYear = delegator.findByAnd("CustomTimePeriod", Static["org.ofbiz.base.util.UtilMisc"].toMap("periodTypeId", "SALES_YEAR"), null, false)!>
	var salesYear = [
	    <#list salesYear as salesYearL>
	    {
	    	customTimePeriodId : "${salesYearL.customTimePeriodId}",
	    	periodName: "${StringUtil.wrapString(salesYearL.get("periodName", locale))}"
	    },
	    </#list>	
	];
	
   	var salesYearData = [];
	<#if salesYear?exists>
		<#list salesYear as salesYearL >
			salesYearData.push({ 'value': '${salesYearL.periodName?if_exists}', 'text': '${StringUtil.wrapString(salesYearL.periodName)?if_exists}'});
		</#list>
	</#if>
	
	var salesMonthData = [
		{'text': '${StringUtil.wrapString(uiLabelMap.BSJanuary)}', 'value': '1'},
		{'text': '${StringUtil.wrapString(uiLabelMap.BSFebruary)}', 'value': '2'},
		{'text': '${StringUtil.wrapString(uiLabelMap.BSMarch)}', 'value': '3'},
		{'text': '${StringUtil.wrapString(uiLabelMap.BSApril)}', 'value': '4'},
		{'text': '${StringUtil.wrapString(uiLabelMap.BSMay)}', 'value': '5'},
		{'text': '${StringUtil.wrapString(uiLabelMap.BSJune)}', 'value': '6'},
		{'text': '${StringUtil.wrapString(uiLabelMap.BSJuly)}', 'value': '7'},
		{'text': '${StringUtil.wrapString(uiLabelMap.BSAugust)}', 'value': '8'},
		{'text': '${StringUtil.wrapString(uiLabelMap.BSSeptember)}', 'value': '9'},
		{'text': '${StringUtil.wrapString(uiLabelMap.BSOctober)}', 'value': '10'},
		{'text': '${StringUtil.wrapString(uiLabelMap.BSNovember)}', 'value': '11'},
		{'text': '${StringUtil.wrapString(uiLabelMap.BSDecember)}', 'value': '12'},
	];
	
	var datee = new Date();
	var getMonth = datee.getMonth();
	var getYear = datee.getFullYear();
	var yearIndex = 0;
    
    for(var i = 0; i <= salesYearData.length; i++){
    	var yearValue = salesYearData[i].value;
    	if(getYear && yearValue && getYear == yearValue){
    		yearIndex = i;
    		break;
    	}
	}
</script>

<#--  <div class="grid">
	<script id="test">
		$(function(){
	        var config = {
				sortable: true,
		    	filterable: true,
		    	showfilterrow: true,
		    	service: 'salesOrder',
	            title: '${StringUtil.wrapString(uiLabelMap.BSSpecialPromoReport)}',
	            columns: [
	        		{ text: '${uiLabelMap.BSNo2}', sortable: false, filterable: false, editable: false, pinned: true, groupable: false, draggable: false, resizable: false,
			    	  datafield: 'stt', columntype: 'number', width: '3%',
			    	  cellsrenderer: function (row, column, value) {
			    		  return '<div style=margin:4px;>' + (value + 1) + '</div>';
		    	  		}
			 		},   
	                { text: '${StringUtil.wrapString(uiLabelMap.BSProductPromo)}', datafield: 'special_promo_id', type: 'string', width: '24%'},
	                { text: '${StringUtil.wrapString(uiLabelMap.BSPromoName)}', datafield: 'special_promo_name', type: 'string', width: '24%'},
	                { text: '${StringUtil.wrapString(uiLabelMap.BSCustomerId)}', datafield: 'customer_code', type: 'string', width: '24%'},
	                { text: '${StringUtil.wrapString(uiLabelMap.BSCustomerName)}', datafield: 'customer_name', type: 'string', width: '25%'},
	            ]
	        };
	
	        var configPopup = [
				{
					action : 'addDropDownList',
					params : [{
						id : 'yearr',
						label : '${StringUtil.wrapString(uiLabelMap.BSYear)}',
						data : salesYearData,
						index: yearIndex
					}]
				},
				{
					action : 'addDropDownList',
					params : [{
						id : 'monthh',
						label : '${StringUtil.wrapString(uiLabelMap.BSMonth)}',
						data : salesMonthData,
						index: getMonth,
					}]
				},
	        ];
	
	
	        var gridEP = OLBIUS.oLapGrid('test', config, configPopup, 'evaluateRegisterSpecialPromotionGrid', true);
	
	        gridEP.funcUpdate(function (oLap) {
	            oLap.update({
                'monthh': oLap.val('monthh'),
	                'yearr': oLap.val('yearr'),
	            });
	        });
	
	        gridEP.init(function () {
	        	gridEP.runAjax();
	        }, false, function(oLap){
	        	var dataAll = oLap.getAllData();
	        	if(dataAll.length != 0){
	            	var getYear = oLap.val('yearr');
	            	var getMonth = oLap.val('monthh');
	            	window.location.href = "exportExhibitionPromotionReportToExcel?yearr=" + getYear + "&monthh=" + getMonth;
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
</div>
-->
<div id="exhibitionPromotionReport"></div>
<script type="text/javascript">
	$(function () {
		var config = {
			title: '${StringUtil.wrapString(uiLabelMap.BSSpecialPromoReport)}',
			button: true,
			service: 'salesOrder',
			id: 'exhibitionPromotionReport',
			olap: 'olapExhibitionPromotionReport',
			sortable: true,
			filterable: true,
			showfilterrow: true,
			columns: [
	        		{ text: '${uiLabelMap.BSNo2}', sortable: false, filterable: false, editable: false, pinned: true, groupable: false, draggable: false, resizable: false,
			    	  datafield: '', columntype: 'number', width: '3%',
			    	  cellsrenderer: function (row, column, value) {
			    		  return '<div style=margin:4px;>' + (value + 1) + '</div>';
		    	  		}
			 		},   
	                { text: '${StringUtil.wrapString(uiLabelMap.BSProductPromo)}', datafield: { name:'special_promo_id', type: 'string'}, width: '24%'},
	                { text: '${StringUtil.wrapString(uiLabelMap.BSPromoName)}', datafield: {name :'special_promo_name', type: 'string'}, width: '24%'},
	                { text: '${StringUtil.wrapString(uiLabelMap.BSCustomerId)}', datafield: {name: 'customer_code', type: 'string'}, width: '24%'},
	                { text: '${StringUtil.wrapString(uiLabelMap.BSCustomerName)}', datafield: {name:'customer_name', type: 'string'}, width: '25%'},
	            
			],
			popup: [
				{
					action : 'jqxDropDownList',
					params : {
						id : 'yearr',
						label : '${StringUtil.wrapString(uiLabelMap.BSYear)}',
						source : salesYearData,
						selectedIndex: yearIndex
					}
				},
				{
					action : 'jqxDropDownList',
					params : {
						id : 'monthh',
						label : '${StringUtil.wrapString(uiLabelMap.BSMonth)}',
						source : salesMonthData,
						selectedIndex: getMonth
					}
				},
			],
			apply: function (grid, popup) {
				var yearr = $("#yearr").val();
				var monthh = $("#monthh").val();
            	var popupData ={
            		yearr: yearr,
            		monthh: monthh
            	}
                return $.extend({
                	'yearr': popup.element("yearr").val(),
                	'monthh': popup.element("monthh").val(),
                }, popupData);
			},
			excel: function(obj){
            	var isExistData = false;
				var dataRow = grid._grid.jqxGrid("getrows");
				if (typeof(dataRow) != 'undefined' && dataRow.length > 0) {
					isExistData = true;
				}
				if (!isExistData) {
					OlbCore.alert.error("${uiLabelMap.BSNoDataToExport}");
					return false;
				}
				
				var otherParam = "";
				if (obj._data) {
					$.each(obj._data, function(key, value){
						otherParam += "&" + key + "=" + value;
					});
				}
				var filterObject = grid.getFilter();
				if (filterObject && filterObject.filter) {
					var filterData = filterObject.filter;
					for (var i = 0; i < filterData.length; i++) {
						otherParam += "&filter=" + filterData[i];
					}
				}
				window.open("exportExhibitionPromotionReportToExcel?" + otherParam, "_blank");
            },
            exportFileName: '[NPP]_BC_DANG_KY_TB_TL_' + (new Date()).formatDate("ddMMyyyy")
		};
		var grid = OlbiusUtil.grid(config);
	});
</script>