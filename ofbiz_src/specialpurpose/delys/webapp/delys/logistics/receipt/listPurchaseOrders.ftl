<script type="text/javascript">
	<#assign statusItems = delegator.findList("StatusItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusTypeId", "ORDER_STATUS"), null, null, null, false)>
	var statusData = new Array();
	<#list statusItems as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.description?if_exists)/>
		row['statusId'] = '${item.statusId?if_exists}';
		row['description'] = '${description?if_exists}';
		statusData[${item_index}] = row;
	</#list>
	<#if (parameters.countryGeoId?has_content)>
	  <#assign countryGeoId = '${parameters.countryGeoId?if_exists}'/>
	<#else>
	  <#assign countryGeoId = ""/>
	</#if>
	function addZero(i) {
	    if (i < 10) {i = "0" + i;}
	    return i;
	}
	function formatFullDate(value) {
		if (value != undefined && value != null && !(/^\s*$/.test(value))) {
			var dateStr = "";
			dateStr += addZero(value.getDate()) + '/';
			dateStr += addZero(value.getMonth()+1) + '/';
			dateStr += addZero(value.getFullYear()) + ' ';
			return dateStr;
		} else {
			return "";
		}
	}
</script>

<#assign dataField="[{ name: 'orderDate', type: 'date', other: 'Timestamp'},
					{ name: 'requirementDate', type: 'date', other: 'Timestamp'},
					{ name: 'orderId', type: 'string'},
					{ name: 'agreementId', type: 'string'},
					{ name: 'partyId', type: 'string'},
					{ name: 'billId', type: 'string'},
					{ name: 'billNumber', type: 'string'},
					{ name: 'containerId', type: 'string'},
					{ name: 'containerNumber', type: 'string'},
					{ name: 'departureDate', type: 'date', other: 'Timestamp'},
					{ name: 'arrivalDate', type: 'date', other: 'Timestamp'},
					{ name: 'orderStatusId', type: 'string'}, 
					{ name: 'currencyUom', type: 'string'},
					{ name: 'grandTotal', type: 'number'}
					]"/>
<#if !countryGeoId?has_content>
<#assign columnlist="
				 { text: '${uiLabelMap.DAOrderId}', dataField: 'orderId', width: '13%', editable: false,
				 	cellsrenderer: function(row, colum, value) {
                   	var data = $('#jqxgrid').jqxGrid('getrowdata', row);
                   	return \"<span><a href='/delys/control/purchaseOrderView?orderId=\" + data.orderId + \"'>\" + data.orderId + \"</a></span>\";
                   }
                },
                { text: '${uiLabelMap.OrderDate}', dataField: 'orderDate', editable: false, width: '13%', cellsformat: 'dd/MM/yyyy', filtertype:'range',
				 },
				 { text: '${uiLabelMap.EstimatedReceiveDate}', dataField: 'requirementDate', editable: false, width: '13%', cellsformat: 'dd/MM/yyyy', filtertype:'range',
				 },
//				 { text: '${uiLabelMap.AgreementId}', dataField: 'agreementId', width: '10%', editable: false, 
//				 	cellsrenderer: function(row, column, value){
//				 		if (value != ''){
//				 			return '<span title=\"' + value +'\">' + value + '</span>';
//				 		} else {
//				 			return '<span title=_NA_> -- </span>';
//				 		}
//				 	}
//				 },
				 { text: '${uiLabelMap.billNumber}', dataField: 'billNumber', width: '15%', editable: false,
					 	cellsrenderer: function(row, column, value){
					 		if (value != ''){
					 			return '<span title=\"' + value +'\">' + value + '</span>';
					 		} else {
					 			return '<span title=_NA_> -- </span>';
					 		}
					 	}
					 },
				 { text: '${uiLabelMap.containerNumber}', dataField: 'containerNumber', width: '15%', editable: false,
					 	cellsrenderer: function(row, column, value){
					 		if (value != ''){
					 			return '<span title=\"' + value +'\">' + value + '</span>';
					 		} else {
					 			return '<span title=_NA_> -- </span>';
					 		}
					 	}
					 },
//				 { text: '${uiLabelMap.departureDate}', dataField: 'departureDate', width: '12%', cellsformat: 'dd/MM/yyyy', filtertype:'date', editable: false,
//					 cellsrenderer: function(row, column, value){
//					 		if (value != ''){
//					 			return \"<span>\" + formatFullDate(value) + \"</span>\";
//					 		} else {
//					 			return '<span title=_NA_> -- </span>';
//					 		}
//					 	}
//				 },
//				 { text: '${uiLabelMap.arrivalDate}', dataField: 'arrivalDate', width: '12%', cellsformat: 'dd/MM/yyyy', filtertype:'date', editable: false,
//					 cellsrenderer: function(row, column, value){
//					 		if (value != ''){
//					 			return \"<span>\" + formatFullDate(value) + \"</span>\";
//					 		} else {
//					 			return '<span title=_NA_> -- </span>';
//					 		}
//					 	}
//				 },
				 { text: '${uiLabelMap.GoodAmount}', dataField: 'grandTotal', width: '15%', editable: false, 
				 	cellsrenderer: function(row, column, value) {
				 		var data = $(\"#jqxgrid\").jqxGrid(\"getrowdata\", row);
				 		var str = \"<div style='text-align:right; width:95%; margin-left:0!important'>\";
						str += formatcurrency(value,data.currencyUom);
						str += \"</div>\";
						return str;
				 	}
				 },
				 { text: '${uiLabelMap.CommonStatus}', dataField: 'orderStatusId', minwidth: '15%',
					 cellsrenderer: function(row, column, value){
						 var data = $('#jqxgrid').jqxGrid('getrowdata', row);
						 if (data.orderStatusId){
							 for(var i = 0; i < statusData.length; i++){
								if(statusData[i].statusId == data.orderStatusId){
									return '<span title=' + data.orderStatusId + '>' + statusData[i].description + '</span>'
								}
							}
						 } 
						 if (data.statusId){
							 for(var i = 0; i < statusData.length; i++){
								if(statusData[i].statusId == data.statusId){
									return '<span title=' + data.statusId + '>' + statusData[i].description + '</span>'
								}
							}
						 }
					 }
				 },
				 "/>
<#else>
<#assign columnlist="{ text: '${uiLabelMap.OrderDate}', dataField: 'orderDate', editable: false, width: '12%', cellsformat: 'dd/MM/yyyy', filtertype:'range',
			},
			{ text: '${uiLabelMap.DAOrderId}', dataField: 'orderId', width: '12%', editable: false,
				cellsrenderer: function(row, colum, value) {
			  	var data = $('#jqxgrid').jqxGrid('getrowdata', row);
			  	return \"<span><a href='/delys/control/purchaseOrderView?orderId=\" + data.orderId + \"&countryGeoId=${countryGeoId}'>\" + data.orderId + \"</a></span>\";
			  }
			},
			{ text: '${uiLabelMap.AgreementId}', dataField: 'agreementId', width: '12%', editable: false, 
				cellsrenderer: function(row, column, value){
					if (value != ''){
						return '<span title=\"' + value +'\">' + value + '</span>';
					} else {
						return '<span title=_NA_> -- </span>';
					}
				}
			},
			{ text: '${uiLabelMap.billNumber}', dataField: 'billNumber', width: '12%', editable: false,
				 	cellsrenderer: function(row, column, value){
				 		if (value != ''){
				 			return '<span title=\"' + value +'\">' + value + '</span>';
				 		} else {
				 			return '<span title=_NA_> -- </span>';
				 		}
				 	}
				 },
			{ text: '${uiLabelMap.containerNumber}', dataField: 'containerNumber', width: '12%', editable: false,
			 	cellsrenderer: function(row, column, value){
			 		if (value != ''){
			 			return '<span title=\"' + value +'\">' + value + '</span>';
			 		} else {
			 			return '<span title=_NA_> -- </span>';
			 		}
			 	}
			 },
			{ text: '${uiLabelMap.GoodAmount}', dataField: 'grandTotal', width: '12%', editable: false, 
				cellsrenderer: function(row, column, value) {
					var data = $(\"#jqxgrid\").jqxGrid(\"getrowdata\", row);
					var str = \"<div style='text-align:right; width:95%; margin-left:0!important'>\";
					str += formatcurrency(value,data.currencyUom);
					str += \"</div>\";
					return str;
				}
			},
			{ text: '${uiLabelMap.CommonStatus}', dataField: 'orderStatusId', minwidth: '10%',
				 cellsrenderer: function(row, column, value){
					 var data = $('#jqxgrid').jqxGrid('getrowdata', row);
					 if (data.orderStatusId){
						 for(var i = 0; i < statusData.length; i++){
							if(statusData[i].statusId == data.orderStatusId){
								return '<span title=' + data.orderStatusId + '>' + statusData[i].description + '</span>'
							}
						}
					 } 
					 if (data.statusId){
						 for(var i = 0; i < statusData.length; i++){
							if(statusData[i].statusId == data.statusId){
								return '<span title=' + data.statusId + '>' + statusData[i].description + '</span>'
							}
						}
					 }
				 }
			},
		"/>
</#if>
<@jqGrid filtersimplemode="true" addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="true"
	showtoolbar="true" addrow="false" deleterow="false" alternativeAddPopup="alterpopupWindow" editable="false" sortable="true" defaultSortColumn="-requirementDate"
	url="jqxGeneralServicer?sname=getJQPurchaseOrders&countryGeoId=${countryGeoId}&orderStatusId=${parameters.statusId?if_exists}&reqStatusId=${parameters.reqStatusId?if_exists}"
/>
			  