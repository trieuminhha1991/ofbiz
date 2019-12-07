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

<div class="grid">
	<script id="test">
		$(function(){
	        var config = {
				sortable: true,
		    	filterable: true,
		    	showfilterrow: true,
	            title: '${StringUtil.wrapString(uiLabelMap.BSSpecialPromoReport)}',
	            columns: [
	        		{ text: '${uiLabelMap.BSNo2}', sortable: false, filterable: false, editable: false, pinned: true, groupable: false, draggable: false, resizable: false,
			    	  datafield: 'stt', columntype: 'number', width: '3%',
			    	  cellsrenderer: function (row, column, value) {
			    		  return '<div style=margin:4px;>' + (value + 1) + '</div>';
		    	  		}
			 		},   
	                { text: '${StringUtil.wrapString(uiLabelMap.BSProductCode)}', datafield: 'product_code', type: 'string', width: '24%'},
	                { text: '${StringUtil.wrapString(uiLabelMap.BSProductName)}', datafield: 'product_name', type: 'string', width: '24%'},
	                { text: '${StringUtil.wrapString(uiLabelMap.BSSalesVolume)}', datafield: 'sales_volume', type: 'string', width: '24%'},
	                { text: '${StringUtil.wrapString(uiLabelMap.BSSalesValue)}', datafield: 'sales_value', type: 'string', width: '25%'},
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
	
	
	        var gridEP = OLBIUS.oLapGrid('test', config, configPopup, 'getTopProductBySalesmanGrid', true);
	
	        gridEP.funcUpdate(function (oLap) {
	            oLap.update({
                'monthh': oLap.val('monthh'),
	                'yearr': oLap.val('yearr'),
	            });
	        });
	
	        gridEP.init(function () {
	        	gridEP.runAjax();
	        });
	    });
	</script>
</div>