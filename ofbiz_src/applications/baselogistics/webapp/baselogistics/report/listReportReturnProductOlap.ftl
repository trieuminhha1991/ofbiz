<script type="text/javascript" src="/crmresources/js/miscUtil.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxeditor.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script> 
<script type="text/javascript" src="/aceadmin/assets/js/bootbox.min.js"></script>
<script type="text/javascript" src="/logresources/js/popup.extend.js"></script>
<script type="text/javascript" src="/logresources/js/popup_extend_grid.js"></script>
<script>
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
					"${item.enumId?if_exists}": "${StringUtil.wrapString(item.get("description", locale)?if_exists)}",
				</#list>
			</#if>	
		};
	
	<#assign productCategorys = delegator.findList("ProductCategory", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("productCategoryTypeId", "CATALOG_CATEGORY"), null, null, null, false)>
	var listProductCategorys = [
		<#if productCategorys?exists>
			<#list productCategorys as item>
			<#if item.productCategoryId != "BROWSE_ROOT">
			{
				productCategoryId: "${item.productCategoryId?if_exists}",
				categoryName: "${StringUtil.wrapString(item.get("categoryName", locale)?if_exists)}"
			},
			</#if>
			</#list>
		</#if>
	];
	
	<#assign returnReasons = delegator.findList("ReturnReason", null, null, null, null, false)>
	var listReturnReason = [
							<#if returnReasons?exists>
								<#list returnReasons as item>
									{
										returnReasonId: "${item.returnReasonId?if_exists}",
										description: "${StringUtil.wrapString(item.get("description", locale)?if_exists)}"
									},
								</#list>
							</#if>
		                      ];
	var mapReturnReasonData = {
			<#if returnReasons?exists>
				<#list returnReasons as item>
					"${item.returnReasonId?if_exists}": "${StringUtil.wrapString(item.get("description", locale)?if_exists)}",
				</#list>
			</#if>	
		};
	
	<#assign returnItemTypes = delegator.findList("ReturnItemType", null, null, null, null, false)>
	var listReturnItemTypes = [
							<#if returnItemTypes?exists>
								<#list returnItemTypes as item>
									{
										returnItemTypeId: "${item.returnItemTypeId?if_exists}",
										description: "${StringUtil.wrapString(item.get("description", locale)?if_exists)}"
									},
								</#list>
							</#if>
		                      ];
	var mapReturnItemTypeData = {
			<#if returnItemTypes?exists>
				<#list returnItemTypes as item>
					"${item.returnItemTypeId?if_exists}": "${StringUtil.wrapString(item.get("description", locale)?if_exists)}",
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
</script>
<script type="text/javascript" id="returnProductReport">
$( document ).ready(function() {
	loadData();
});

function loadData(){
	var listFacilityDataSource = [];
	var listProductCategoryDataSource =[];
	var listEnumerationDataSource = [];
	var listReturnReasonDataSource = [];
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
	for(var i in listReturnReason){
		var returnReasonDataSource = {
			text: listReturnReason[i].description,
			value: listReturnReason[i].returnReasonId,
		}
		listReturnReasonDataSource.push(returnReasonDataSource);
	}
	bindingDataToReport(listFacilityDataSource, listProductCategoryDataSource, listEnumerationDataSource, listReturnReasonDataSource);
}

function bindingDataToReport(listFacilityDataSource, listProductCategoryDataSource, listEnumerationDataSource, listReturnReasonDataSource){
	var config = {
          title: ' ${uiLabelMap.CustomerReturnReport}',
          service: 'returnItem',
          columns: [
              { text: '${uiLabelMap.LogDateReturn}', datafield: 'date', type: 'olapDate', width: 120,  align: 'center', cellsalign: 'left',},
              { text: '${uiLabelMap.LogCodeReturns}', datafield: 'returnId', type: 'string', width: 120,
              },
              { text: '${uiLabelMap.OrderId}', datafield: 'orderId', type: 'string', width: 150,
              },
              { text: '${uiLabelMap.ProductCode}', datafield: 'productId', type: 'string', width: 150,
              },
              { text: '${uiLabelMap.ProductName}', datafield: 'productName', width: 250, type: 'string', 
              },
              { text: '${uiLabelMap.QuantityReturned}', datafield: 'returnQuantity', width: 150, type: 'number', cellsalign: 'right',},
              { text: '${uiLabelMap.Unit}', datafield: 'quantityUomId', width: 80, type: 'number', cellsalign: 'right',
            	  cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
		    		  if(value){
		    			  return '<span>' + mapUomData[value] + '</span>';
		    		  }
		          }
              },
              { text: '${uiLabelMap.UnitPrice}', datafield: 'returnPrice', width: 150, type: 'number', cellsalign: 'right',
            	  cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
            		  return '<span style=\"text-align: right\">' + formatcurrency(value) + '</span>';
            	  }
              },
              { text: '${uiLabelMap.Facility}', datafield: 'facilityId',  width: 180,
	      	  },
              { text: '${uiLabelMap.LogRejectReasonReturnProduct}', datafield: 'returnReasonId', type: 'string', width: 250,
              	cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
		    		  if(value){
		    			  return '<span>' + mapReturnReasonData[value] + '</span>';
		    		  }
		          	}
              }, 
              { text: '${uiLabelMap.LogProductReturns}', datafield: 'partyFromId', type: 'string', width: 350,
              },
              { text: '${uiLabelMap.Receiver}', datafield: 'partyToId', type: 'string', width: 150,
              },
              { text: '${uiLabelMap.LogGoodReturned}', datafield: 'returnItemTypeId', type: 'string', width: 250,
              	cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
		    		  if(value){
		    			  return '<span>' + mapReturnItemTypeData[value] + '</span>';
		    		  }
		          	}
              },
		      { text: '${uiLabelMap.Status}', datafield: 'statusId', type: 'string', width: 150,
	          },
	      	  { text: '${uiLabelMap.LogPurchaseChannels}', datafield: 'productStoreId', type: 'string', width: 200,
		      		cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
		    		  if(value){
		    			  if(mapEnumerationData[value] != undefined){
		    				  return '<span>' + mapEnumerationData[value] + '</span>';
		    			  }
		    			  else if(mapProductStoresData[value] != undefined){
		    				  return '<span>' + mapProductStoresData[value] + '</span>';
		    			  }
		    			  else{
		    				  return '<span>' + value + '</span>';
		    			  }
		    		  }
		          	}
	      	  },
	      	  { text: '${uiLabelMap.DmsProductCatalogs}', datafield: 'categoryName', type: 'string', width: 200,
	      	  },
          ]
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
              id : 'dateType',
              label : '${StringUtil.wrapString(uiLabelMap.CommonPeriod)}',
              data : date_type_source,
              index: 0
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
      {
	      action : 'addJqxGridMultil',
	      params : [{
	          id : 'enumId',
	          title1: '${StringUtil.wrapString(uiLabelMap.LogCodeChannel)}',
	          title2: '${StringUtil.wrapString(uiLabelMap.LogPurchaseChannels)}',  
	          label : '${StringUtil.wrapString(uiLabelMap.LogPurchaseChannels)}',
	          data : listEnumerationDataSource,
	          value: []
	      }]
      },
      {
	      action : 'addJqxGridMultil',
	      params : [{
	          id : 'returnReasonId',
	          title1: '${StringUtil.wrapString(uiLabelMap.LogReturnReasonId)}',
	          title2: '${StringUtil.wrapString(uiLabelMap.LogRejectReasonReturnProduct)}',  
	          label : '${StringUtil.wrapString(uiLabelMap.LogRejectReasonReturnProduct)}',
	          data : listReturnReasonDataSource,
	          value: []
	      }]
      },
  ];
  
  var testGrid = OLBIUS.oLapGrid('returnProductReport', config, configPopup, 'jqGetListReturnProductReportOlap', true); 

  testGrid.funcUpdate(function (oLap) {
      oLap.update({
          'dateType': oLap.val('dateType'),
          'fromDate': oLap.val('from_date'),
          'thruDate': oLap.val('thru_date'),
          'facilityId': oLap.val('facilityId'),
          'categoryId': oLap.val('categoryId'),
          'enumId': oLap.val('enumId'),
          'returnReasonId': oLap.val('returnReasonId'),
          'checkNPP': "NPP_FALSE",
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
      	var facilityIdInput = oLap.val('facilityId');
      	var categoryIdInput = oLap.val('categoryId');
      	var enumIdInput = oLap.val('enumId');
      	var returnReasonIdInput = oLap.val('returnReasonId');
      	var dateFromDate = new Date(fromDateInput);
      	var dateThruDate = new Date(thruDateInput);
      	var dateFrom = dateFromDate.getTime();
      	var thruFrom = dateThruDate.getTime();
      	var checkNPP = "NPP_FALSE";
      	window.location.href = "exportReturnProductOlapLogToPdf?dateType=" + dateTypeInput + "&fromDate=" + dateFrom + "&thruDate=" + thruFrom + "&productId=" + productIdInput + "&facilityId=" + facilityIdInput + "&categoryId=" + categoryIdInput + "&enumId=" + enumIdInput + "&returnReasonId=" + returnReasonIdInput + "&checkNPP=" + checkNPP;
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
      	var facilityIdInput = oLap.val('facilityId');
      	var categoryIdInput = oLap.val('categoryId');
      	var filterTypeIdInput = oLap.val('filterTypeId');
      	var enumIdInput = oLap.val('enumId');
      	var returnReasonIdInput = oLap.val('returnReasonId');
      	var dateFromDate = new Date(fromDateInput);
      	var dateThruDate = new Date(thruDateInput);
      	var dateFrom = dateFromDate.getTime();
      	var thruFrom = dateThruDate.getTime();
      	var checkNPP = "NPP_FALSE";
      	window.location.href = "exportReturnProductOlapLogToExcel?dateType=" + dateTypeInput + "&fromDate=" + dateFrom + "&thruDate=" + thruFrom + "&productId=" + productIdInput + "&facilityId=" + facilityIdInput + "&categoryId=" + categoryIdInput + "&enumId=" + enumIdInput + "&filterTypeId=" + filterTypeIdInput + "&returnReasonId=" + returnReasonIdInput + "&checkNPP=" + checkNPP;
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

<script type="text/javascript" id="returnProductPieChart">
$(function () {
	var optionFilterDataType = [
	      { 
	    	  text: '${uiLabelMap.DmsProductCatalogs}',
	    	  value: "FILTER_CATALOG" 
	      },
	      {
	    	  text: '${uiLabelMap.LogProductReturns}', 
	    	  value: "FILTER_RETURNER"  
	      }
	]
    var config = {
        chart: {
            plotBackgroundColor: null,
            plotBorderWidth: null,
            plotShadow: false
        },
        title: {
            text: '${StringUtil.wrapString(uiLabelMap.CustomerReturnReportByCatalogChart)}'
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
            event : function(popup) {
                popup.onEvent('filterTypeId', 'select', function(event) {
                	var title = '${StringUtil.wrapString(uiLabelMap.ChartLineReceiveExportInventory)}';
                    var args = event.args;
                    var item = popup.item('filterTypeId', args.index);
                    var filter = item.value;
                    if(filter == "FILTER_CATALOG"){  
                    	titleDetailProduct = '${StringUtil.wrapString(uiLabelMap.CustomerReturnReportByCatalogChart)}';
                    }else{
                    	titleDetailProduct = '${StringUtil.wrapString(uiLabelMap.LogRateReturnProductByCustomer)}';
                    }
                    returnProductPieChart.resetTitle(titleDetailProduct)
                    popup.resize();
                });
            }
        },
    ];
    var returnProductPieChart = OLBIUS.oLapChart('returnProductPieChart', config, configPopup, 'returnProductReportPieChart', true, true, OLBIUS.defaultPieFunc);

    returnProductPieChart.funcUpdate(function(oLap) {
		
        oLap.update({
            'fromDate': oLap.val('from_date'),
            'thruDate': oLap.val('thru_date'),
            'filterTypeId': oLap.val('filterTypeId'),
            'checkNPP': "NPP_FALSE",
        });
    });

    returnProductPieChart.init(function () {
    	returnProductPieChart.runAjax();
    });
});
</script>