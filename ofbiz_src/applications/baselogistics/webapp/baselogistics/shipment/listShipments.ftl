<#include "script/shipmentScript.ftl"/>
<#assign filedShipment="[{ name: 'shipmentId', type: 'string'},
					{ name: 'shipmentMethodTypeId', type: 'string'},
					{ name: 'originFacilityId', type: 'string'},
					{ name: 'destinationFacilityId', type: 'string'},
					{ name: 'originContactMechId', type: 'string'},
					{ name: 'destinationContactMechId', type: 'string'},
					{ name: 'originFacilityName', type: 'string'},
					{ name: 'destFacilityName', type: 'string'},
					{ name: 'originAddress', type: 'string'},
					{ name: 'destAddress', type: 'string'},
					{ name: 'handlingIntructions', type: 'string'},
					{ name: 'defaultWeightUomId', type: 'string'},
					{ name: 'currencyUomId', type: 'string'},
					{ name: 'estimatedShipCost', type: 'number'},
					{ name: 'estimatedReadyDate', type: 'date', other: 'Timestamp'},
					{ name: 'estimatedShipDate', type: 'date', other: 'Timestamp'},
					{ name: 'estimatedArrivalDate', type: 'date', other: 'Timestamp'},
					{ name: 'actualShipDate', type: 'date', other: 'Timestamp'},
					{ name: 'actualArrivalDate', type: 'date', other: 'Timestamp'},
					{ name: 'statusId', type: 'string'},
				   ]"/>
<#assign columnShipment="
	{ text: '${uiLabelMap.SequenceId}', sortable: false, filterable: false, editable: false, pinned: true,
	    groupable: false, draggable: false, resizable: false,
	    datafield: '', columntype: 'number', width: 50,
	    cellsrenderer: function (row, column, value) {
	        return '<div style=margin:4px;>' + (value + 1) + '</div>';
	    }
	},
	{ text: '${StringUtil.wrapString(uiLabelMap.ShipmentId)}', datafield: 'shipmentId', pinned: true, width:150,
		cellsrenderer: function(row, colum, value){
			var link = 'viewShipmentDetail?shipmentId=' + value;
        	return '<span><a target=\"_blank\" href=\"' + link + '\">' + value + '</a></span>';
		}
	},
	{ text: '${uiLabelMap.Status}', datafield: 'statusId', align: 'left', width: 150, filtertype: 'checkedlist',
		cellsrenderer: function(row, colum, value){
			for(i=0; i < statusData.length; i++){
	            if(statusData[i].statusId == value){
	            	return '<span style=\"text-align: left;\" title='+value+'>' + statusData[i].description + '</span>';
	            }
	        }
		},
		createfilterwidget: function (column, columnElement, widget) {
			var tmp = statusData;
			var filterDataAdapter = new $.jqx.dataAdapter(tmp, {
				autoBind: true
			});
			var records = filterDataAdapter.records;
			widget.jqxDropDownList({source: records, displayMember: 'statusId', valueMember: 'statusId',
				renderer: function(index, label, value){
		        	if (tmp.length > 0) {
						for(var i = 0; i < tmp.length; i++){
							if(tmp[i].statusId == value){
								return '<span>' + tmp[i].description + '</span>';
							}
						}
					}
					return value;
				}
			});
			widget.jqxDropDownList('checkAll');
		},
	},
	{ text: '${StringUtil.wrapString(uiLabelMap.OriginFacility)}', datafield: 'originFacilityName', width:150,
		cellsrenderer: function (row, colum, value){
	 	}
	},
	{ text: '${StringUtil.wrapString(uiLabelMap.DestFacility)}', datafield: 'destFacilityName', width:150,
		cellsrenderer: function (row, colum, value){
	 	}
	},
	{ text: '${StringUtil.wrapString(uiLabelMap.OriginAddress)}', datafield: 'originAddress', width:150,
		cellsrenderer: function (row, colum, value){
	 	}
	},
	{ text: '${StringUtil.wrapString(uiLabelMap.DestAddress)}', datafield: 'destAddress', width:150,
		cellsrenderer: function (row, colum, value){
	 	}
	},
	{ text: '${StringUtil.wrapString(uiLabelMap.EstimatedShipCost)}', datafield: 'estimatedShipCost', width:150,
		cellsrenderer: function (row, colum, value){
			if (value){
				var data = $('#jqxgridShipment').jqxGrid('getrowdata', row);
				var uomDes = '';
				for (var i=0; i < quantityUomData.length; i++){
					 if (data.baseQuantityUomId == quantityUomData[i].quantityUomId){
						 uomDes = quantityUomData[i].description;
					 }
				}
				var localeQuantity = parseInt(value);
				return '<span style=\"text-align: right\" title=' + localeQuantity.toLocaleString('${localeStr}') + '>' + localeQuantity.toLocaleString('${localeStr}') + ' ('+uomDes+')</span>'
	 		} else {
 				return '<span>_NA_</span>';
	 		}
	 	}
	},
	{ text: '${StringUtil.wrapString(uiLabelMap.EstimatedReadyDate)}', dataField: 'estimatedReadyDate', align: 'left', width: 150, columntype: 'datetimeinput', filtertype: 'range', cellsformat: 'dd/MM/yyyy', cellsalign: 'right',
		cellsrenderer: function (row, colum, value){
	 		var data = $('#jqxgridShipment').jqxGrid('getrowdata', row);
	 		if (value){
	 			return '<span style=\"text-align: right\">'+ DatetimeUtilObj.formatFullDate(value)+'</span>';
	 		} else {
 				return '<span>_NA_</span>';
	 		}
	 	}
	},
	{ text: '${StringUtil.wrapString(uiLabelMap.EstimatedShipDate)}', dataField: 'estimatedShipDate', align: 'left', width: 150, columntype: 'datetimeinput', filtertype: 'range', cellsformat: 'dd/MM/yyyy', cellsalign: 'right',
		cellsrenderer: function (row, colum, value){
	 		var data = $('#jqxgridShipment').jqxGrid('getrowdata', row);
	 		if (value){
	 			return '<span style=\"text-align: right\">'+ DatetimeUtilObj.formatFullDate(value)+'</span>';
	 		} else {
 				return '<span>_NA_</span>';
	 		}
	 	}
	},
	{ text: '${StringUtil.wrapString(uiLabelMap.EstimatedArrivalDate)}', dataField: 'estimatedArrivalDate', align: 'left', width: 150, columntype: 'datetimeinput', filtertype: 'range', cellsformat: 'dd/MM/yyyy', cellsalign: 'right',
		cellsrenderer: function (row, colum, value){
	 		var data = $('#jqxgridShipment').jqxGrid('getrowdata', row);
	 		if (value){
	 			return '<span style=\"text-align: right\">'+ DatetimeUtilObj.formatFullDate(value)+'</span>';
	 		} else {
 				return '<span>_NA_</span>';
	 		}
	 	}
	},
	{ text: '${StringUtil.wrapString(uiLabelMap.ActualStartDelivery)}', dataField: 'actualShipDate', align: 'left', width: 150, columntype: 'datetimeinput', filtertype: 'range', cellsformat: 'dd/MM/yyyy', cellsalign: 'right',
		cellsrenderer: function (row, colum, value){
	 		var data = $('#jqxgridShipment').jqxGrid('getrowdata', row);
	 		if (value){
	 			return '<span style=\"text-align: right\">'+ DatetimeUtilObj.formatFullDate(value)+'</span>';
	 		} else {
 				return '<span>_NA_</span>';
	 		}
	 	}
	},
	{ text: '${StringUtil.wrapString(uiLabelMap.ActualDeliveredDate)}', dataField: 'actualArrivalDate', align: 'left', width: 150, columntype: 'datetimeinput', filtertype: 'range', cellsformat: 'dd/MM/yyyy', cellsalign: 'right',
		cellsrenderer: function (row, colum, value){
	 		var data = $('#jqxgridShipment').jqxGrid('getrowdata', row);
	 		if (value){
	 			return '<span style=\"text-align: right\">'+ DatetimeUtilObj.formatFullDate(value)+'</span>';
	 		} else {
 				return '<span>_NA_</span>';
	 		}
	 	}
	},
	{ text: '${StringUtil.wrapString(uiLabelMap.ShipmentMethod)}', datafield: 'shipmentMethodTypeId', width:150,
		cellsrenderer: function (row, colum, value){
	 		var data = $('#jqxgridShipment').jqxGrid('getrowdata', row);
	 		if (value){
	 			for (var i = 0; i < shipmentMethodData.length; i++){
	 				if (value == shipmentMethodData[i].shipmentMethodTypeId){
	 					return '<span>'+shipmentMethodData[i].description+'</span>';
	 				}
	 			}
	 		} else {
 				return '<span>_NA_</span>';
	 		}
	 	}
	},
	"/>

<div id="notifyContainer" >
	<div id="notifyContainer">
	</div>
</div>
<div>	
	<@jqGrid filtersimplemode="true" id="jqxgridShipment" addType="popup" dataField=filedShipment columnlist=columnShipment clearfilteringbutton="true"
		showtoolbar="true" addrow="false" deleterow="false" alternativeAddPopup="" editable="false" addrefresh="true"
		url="jqxGeneralServicer?sname=getShipments&payToPartyId=${company}&facilityId=${parameters.facilityId?if_exists}&shipmentTypeId=${parameters.shipmentTypeId?if_exists}" addColumns=""
		createUrl="" mouseRightMenu="true" contextMenuId="" jqGridMinimumLibEnable="false"
		showlist="true" customTitleProperties="${title}"/>
</div>