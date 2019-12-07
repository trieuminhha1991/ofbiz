<div id="olbiusChartLineFluctuationInventory"></div>
<div style="text-align: center">
	<b>
		<div><span id="title-fa" class="hide">${StringUtil.wrapString(uiLabelMap.Facility)}: </span><span id="facilityPicked"></span></div>
		<div><span id="title-pr" class="hide">${StringUtil.wrapString(uiLabelMap.Product)}: </span><span id="productPicked"></span></div>
	</b>
</div>
<script type="text/javascript">
	$(function(){
		var config = {
			service: "facilityInventory",
            id: "olbiusChartLineFluctuationInventory",
            olap: "olapChartLineFluctuationInventory",
            
            title: {
                text: '${StringUtil.wrapString(uiLabelMap.BLChartFluctuationInventory)}',
                x: -20 //center
            },
            xAxis: {
                labels: {
                    enabled: false
                },
                title: {
                    text: null
                }
            },
            yAxis: {
                plotLines: [{
                    value: 0,
                    width: 1,
                    color: '#808080'
                }],
                title: {
                    text: null
                },
                min: 0
            },
            tooltip: {
                formatter: function () {
                    return '<tspan style="fill:' + this.color + '" x="8" dy="15"></tspan><i> ' + this.series.name + '</i>: <b>' + this.y.toLocaleString(locale) + '</b>';
                }
            },
            legend: {
				layout: 'vertical',
				align: 'right',
				verticalAlign: 'middle',
				borderWidth: 0
            },
            chartRender: OlbiusUtil.getChartRender('defaultLineFunc'),
            
            popup: [
				{
				    action: 'jqxGridElement',
				    params: {
				        id: 'facility',
				        label: "${StringUtil.wrapString(uiLabelMap.Facility)}",
				        grid: {
				        	url: 'jqGetFacilities&facilityGroupId=FACILITY_INTERNAL',
				        	id: 'facilityId',
				        	width: 550,
				        	sortable: true,
				            pagesize: 5,
				            columnsresize: true,
				            pageable: true,
				            altrows: true,
				            showfilterrow: true,
				             placeHolder: '${StringUtil.wrapString(uiLabelMap.PleaseSelectTitle)}',
				            showtoolbar: false,
				            filterable: true,
				            datafields: [{name: 'facilityId', type: 'string'}, {name: 'facilityCode', type: 'string'}, {name: 'facilityName', type: 'string'}],
				            displayField: 'facilityName',
				            displayAdditionField: 'facilityCode',
				        	columns: [
					          	{ text: "${StringUtil.wrapString(uiLabelMap.FacilityId)}", datafield: 'facilityCode', width: 150 }, 
								{ text: "${StringUtil.wrapString(uiLabelMap.FacilityName)}", datafield: 'facilityName' }
					        ]
				        }
				    },
				    hide: false
				},     
				{
				    action: 'jqxGridElement',
				    params: {
				        id: 'product',
				        label: "${StringUtil.wrapString(uiLabelMap.Product)}",
				        grid: {
				        	url: 'JQGetPOListProducts',
				        	id: 'productId',
				        	width: 550,
				        	sortable: true,
				            pagesize: 5,
				            columnsresize: true,
				            pageable: true,
				            showtoolbar: false,
				            altrows: true,
				            showfilterrow: true,
				            placeHolder: '${StringUtil.wrapString(uiLabelMap.PleaseSelectTitle)}',
				            filterable: true,
				            datafields: [{name: 'productId', type: 'string'}, {name: 'productCode', type: 'string'}, {name: 'productName', type: 'string'}],
				            displayField: 'productName',
				            displayAdditionField: 'productCode',
				        	columns: [
					          	{ text: "${StringUtil.wrapString(uiLabelMap.ProductId)}", datafield: 'productCode', width: 150 }, 
								{ text: "${StringUtil.wrapString(uiLabelMap.ProductName)}", datafield: 'productName' }
					        ]
				        }
				    },
				    hide: false
				},
				{	
            		action: 'jqxDropDownList',
                    params: {
                        id: 'numYear',
                        label: "${StringUtil.wrapString(uiLabelMap.BLNumYear)}",
                        source: [{'text': '1', 'value': '1'}, {'text': '2', 'value': '2'}, {'text': '3', 'value': '3'}, {'text': '4', 'value': '4'}, {'text': '5', 'value': '5'}],
                        selectedIndex: 2
                    }
                },
				{	
            		action: 'jqxDropDownList',
                    params: {
                        id: 'inventoryType',
                        label: "${StringUtil.wrapString(uiLabelMap.BLType)}",
                        source: [{'text': '${StringUtil.wrapString(uiLabelMap.facility_inventory)}', 'value': 'TOTAL'}, {'text': '${StringUtil.wrapString(uiLabelMap.facility_receive)}', 'value': 'RECEIVE'}, {'text': '${StringUtil.wrapString(uiLabelMap.facility_export)}', 'value': 'EXPORT'}],
                        selectedIndex: 0
                    }
                },
            ],
            
            apply: function (grid, popup) {
            	if (popup.val("product")) {
            		var gridProduct = popup.element("product").Gridelement;
	            	var indexP = gridProduct.jqxGrid('getselectedrowindex');
	            	var dataP = gridProduct.jqxGrid('getrowdata', indexP);
	            	
            		var gridFacility = popup.element("facility").Gridelement;
	            	var indexF = gridFacility.jqxGrid('getselectedrowindex');
	            	var dataF = gridFacility.jqxGrid('getrowdata', indexF);
	            	
	            	updateDisplay({
	                	'facility': dataF,
						'product': dataP,
	            	});
            	}
                return {
                	'olapType': 'LINECHART',
                	'facilityId': popup.val("facility"),
					'productId': popup.val("product"),
					'numYear': popup.val("numYear"),
					'inventoryType': popup.val("inventoryType"),
                };
            },
        };
		
		OlbiusUtil.chart(config);
	});
	
	function updateDisplay (data) {
		if (data.facility) {
			$("#facilityPicked").text(data.facility.facilityCode);
			$("#title-fa").removeClass("hide");
		}
		if (data.product) {
			$("#productPicked").text(data.product.productCode + " - " + data.product.productName);
			$("#title-pr").removeClass("hide");
		}
 	} 
</script>
