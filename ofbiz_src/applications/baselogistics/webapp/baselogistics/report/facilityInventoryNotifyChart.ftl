<script type="text/javascript" src="/crmresources/js/miscUtil.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxeditor.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script> 
<script type="text/javascript" src="/aceadmin/assets/js/bootbox.min.js"></script>
<script type="text/javascript" src="/poresources/js/popup.extend.js"></script>
<script type="text/javascript" src="/aceadmin/assets/js/initDashboard.js"></script>
<script type="text/javascript" src="/poresources/js/popup_extend_grid.js"></script>
<script>
	<#assign products = delegator.findList("Product", null, null, null, null, false)!>
	<#assign ownerPartyId = Static["com.olbius.basehr.util.PartyUtil"].getCurrentOrganization(delegator, userLogin.userLoginId)/>
	<#assign facilitys = delegator.findList("Facility", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("facilityTypeId", "WAREHOUSE", "ownerPartyId", ownerPartyId)), null, null, null, false)>
	var faciData = new Array();
	<#list facilitys as item>
		var row = {};
		<#assign facilityName = StringUtil.wrapString(item.facilityName?if_exists)/>
		row['facilityId'] = '${item.facilityId?if_exists}';
		row['facilityName'] = '${facilityName?if_exists}';
		faciData[${item_index}] = row;
	</#list>
	
	var listFacilityData = [
								<#if facilitys?exists>
									<#list facilitys as item>
										{
											facilityId: "${item.facilityId?if_exists}",
											facilityName: "${StringUtil.wrapString(item.get("facilityName", locale)?if_exists)}"
										},
									</#list>
								</#if>
			                      ];
	var mapFacilityData = {
			<#if facilitys?exists>
				<#list facilitys as item>
					"${item.facilityId?if_exists}": '${StringUtil.wrapString(item.get("facilityName", locale)?if_exists)}',
				</#list>
			</#if>	
		};
</script>
<script type="text/javascript" id="inventoryNotify">
	function  setColumnFunc(data, chart, datetype, removeSeries, flagFunc, olap) {
	    var tmp = {
	        labels: {
	            enabled: true
	        },
	        categories: data.xAxis
	    };
	
	    chart.xAxis[0].update(tmp, false);
	
	    if (removeSeries) {
	        while (chart.series.length > 0) {
	            chart.series[0].remove(false);
	        }
	    }
	    var color = 0;
	    for (var i in data.yAxis) {
	        chart.addSeries({
	            name: i,
	            data: data.yAxis[i],
	            color: '#EE0000',
	        }, false);
	    }
	
	    chart.redraw();
	
	    if (data.xAxis != undefined && data.xAxis && data.xAxis.length == 0) {
	        flagFunc();
	    }
	}

	$(function () {
    	var productData = [
	   	   	<#if products?exists>
	   	   		<#list products as item>
	   	   			{
	   	   				productId: '${item.productCode?if_exists}',
	   	   				<#assign s = StringUtil.wrapString(item.get('productName', locale)?if_exists)/>
	   	   				productName: "${s}"
	   	   			},
	   	   		</#list>
	   	   	</#if>
        ];
    	
    	var listProductDataSource = [];	
    	var listFacilityDataSource = [];
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
    	var config = {  
                chart: {
                    type: 'column'
                },
                title: {
                    text: '${StringUtil.wrapString(uiLabelMap.ChatInventoryWarning)}',
                    x: -20 // center
                },
                xAxis: {
                    type: 'category',
                    labels: {
                        rotation: -45,
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
                        color: '#EE0000'
                    }],
                    title : {
                        text: null
                    },
                    min: 0
                },
                legend: {
                    enabled: false
                },
                tooltip: {
                    pointFormat: '{point.y}'
                }
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
   			];
    	
    	var inventoryNotityCharOlap = OLBIUS.oLapChart('inventoryNotify', config, configPopup, 'inventoryNotityChartOlap', true, true, setColumnFunc);

    	inventoryNotityCharOlap.funcUpdate(function (oLap) {
            oLap.update({
                'productId': oLap.val('productId'),
                'facilityId': oLap.val('facilityId'),
                'typeChart': 'quantity',
            });
        });
    	inventoryNotityCharOlap.init(function () {
    		inventoryNotityCharOlap.runAjax();
        });
    });
    
</script>