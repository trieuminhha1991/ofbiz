<script type="text/javascript" src="/crmresources/js/miscUtil.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxeditor.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script> 
<script type="text/javascript" src="/aceadmin/assets/js/bootbox.min.js"></script>
<script type="text/javascript" src="/logresources/js/popup.extend.js"></script>
<script type="text/javascript" src="/logresources/js/popup_extend_grid.js"></script>
<script>
//Prepare for product data
	<#assign ownerPartyId = Static["com.olbius.basehr.util.PartyUtil"].getCurrentOrganization(delegator, userLogin.userLoginId)/>
	<#assign products = delegator.findList("Product", null, null, null, null, false)!>
	var productData2 = [
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
	
	<#assign transferTypes = delegator.findList("TransferType", null, null, null, null, false)>
	var mapTransferTypesData = {
			<#if transferTypes?exists>
				<#list transferTypes as item>
					"${item.transferTypeId?if_exists}": "${StringUtil.wrapString(item.get("description", locale)?if_exists)}",
				</#list>
			</#if>	
	};
	
	<#assign statusTransfers = delegator.findList("StatusItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusTypeId", "TRANSFER_ITEM_STATUS"), null, null, null, false)>
	var mapStatusTransfersData = {
			<#if statusTransfers?exists>
				<#list statusTransfers as item>
					"${item.statusId?if_exists}": "${StringUtil.wrapString(item.get("description", locale)?if_exists)}",
				</#list>
			</#if>	
	};
	
	<#assign statusDeliverys = delegator.findList("StatusItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusTypeId", "DELIVERY_ITEM_STATUS"), null, null, null, false)>
	var mapStatusDeliverysData = {
			<#if statusDeliverys?exists>
				<#list statusDeliverys as item>
					"${item.statusId?if_exists}": "${StringUtil.wrapString(item.get("description", locale)?if_exists)}",
				</#list>
			</#if>	
	};
</script>
<script type="text/javascript" id="transferItem">
$( document ).ready(function() {
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
	loadProduct(productData);
});

function loadProduct(productData){
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
	
	bindingDataToReport(listProductDataSource, listFacilityDataSource);
}

function bindingDataToReport(listProductDataSource, listFacilityDataSource){
	var config = {
          title: ' ${uiLabelMap.ReportTransfer}',
          service: 'transferItem',
          columns: [
              { text: '${uiLabelMap.RequiredByDate}', datafield: 'date', type: 'olapDate', width: 150,  align: 'center',},
              { text: '${uiLabelMap.TransferId}', datafield: 'transferId', type: 'string', width: 120,
              },
              { text: '${uiLabelMap.TransferType}', datafield: 'transferTypeId', type: 'string', width: 180,
            	  cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
		    		  if(value){
		    			  return '<span>' + mapTransferTypesData[value] + '</span>';
		    		  }
		          }
              }, 
              { text: '${uiLabelMap.FacilityFrom}', datafield: 'originFacilityName', type: 'string', width: 200,
              },
              { text: '${uiLabelMap.FacilityTo}', datafield: 'destFacilityName', type: 'string', width: 200,
              },
              { text: '${uiLabelMap.ProductCode}', datafield: 'productCode', width: 150, type: 'string', 
              },
              { text: '${uiLabelMap.ProductName}', datafield: 'productName', width: 350, type: 'string', 
              }, 
              { text: '${uiLabelMap.ProductManufactureDate}', datafield: 'datetimeManufactured', type: 'string',  align: 'center', width: 150,},
              { text: '${uiLabelMap.ExpireDate}', datafield: 'expireDate', type: 'string',  align: 'center', width: 150,},
              { text: '${uiLabelMap.RequiredNumber}', datafield: 'quantity', width: 120, type: 'number', cellsalign: 'right',},
              { text: '${uiLabelMap.LogShipmentStatus}', datafield: 'statusId', width: 180, type: 'string',
            	  cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
		    		  if(value){
		    			  return '<span>' + mapStatusTransfersData[value] + '</span>';
		    		  }
		          }
              },
              { text: '${uiLabelMap.DeliveryTransferId}', datafield: 'deliveryId', width: 150, type: 'string',},
              { text: '${uiLabelMap.StatusDelivery}', datafield: 'deliveryStatusId', width: 200, type: 'string',
            	  cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
		    		  if(value){
		    			  return '<span>' + mapStatusDeliverysData[value] + '</span>';
		    		  }
		          }
              }, 
              { text: '${uiLabelMap.ActualExportedQuantity}', datafield: 'actualExportedQuantity', width: 150, type: 'number', cellsalign: 'right',},
              { text: '${uiLabelMap.QuantityUomId}', datafield: 'uomId', width: 150, type: 'string',},
              { text: '${uiLabelMap.CreatedBy}', datafield: 'partyName', width: 250, type: 'string',},
              { text: '${uiLabelMap.Batch}', datafield: 'lotId', width: 150, type: 'string',},
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
  
  var testGrid = OLBIUS.oLapGrid('transferItem', config, configPopup, 'jqGetListTransferItemReportOlap', true); 

  testGrid.funcUpdate(function (oLap) {
      oLap.update({
          'dateType': oLap.val('dateType'),
          'fromDate': oLap.val('from_date'),
          'thruDate': oLap.val('thru_date'),
          'productId': oLap.val('productId'),
          'facilityId': oLap.val('facilityId'),
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
      	var dateFromDate = new Date(fromDateInput);
      	var dateThruDate = new Date(thruDateInput);
      	var dateFrom = dateFromDate.getTime();
      	var thruFrom = dateThruDate.getTime();
      	window.location.href = "exportTransferItemOlapToPdf?dateType=" + dateTypeInput + "&fromDate=" + dateFrom + "&thruDate=" + thruFrom + "&productId=" + productIdInput + "&facilityId=" + facilityIdInput;
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
      	var dateFromDate = new Date(fromDateInput);
      	var dateThruDate = new Date(thruDateInput);
      	var dateFrom = dateFromDate.getTime();
      	var thruFrom = dateThruDate.getTime();
      	window.location.href = "exportTransferItemOlapToExcel?dateType=" + dateTypeInput + "&fromDate=" + dateFrom + "&thruDate=" + thruFrom + "&productId=" + productIdInput + "&facilityId=" + facilityIdInput;
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

