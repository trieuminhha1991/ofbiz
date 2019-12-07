<#include "script/detailReturnScript.ftl"/>
<div id="product-tab" class="tab-pane<#if !activeTab?exists || activeTab == "" || activeTab == "product-tab"> active</#if>">
<#assign columnlist="
	{ text: '${uiLabelMap.SequenceId}', sortable: false, filterable: false, editable: false, pinned: true,
	    groupable: false, draggable: false, resizable: false,
	    datafield: '', columntype: 'number', width: 50,
	    cellsrenderer: function (row, column, value) {
	        return '<div style=margin:4px;>' + (value + 1) + '</div>';
	    }
	},		
	{ text: '${uiLabelMap.ProductId}', pinned: true, dataField: 'productCode', width: 150, editable:false, 
	},
	{ text: '${uiLabelMap.ProductName}', dataField: 'productName', minwidth: 200, editable:false,
	},"/>
	<#if returnHeader.returnHeaderTypeId == "CUSTOMER_RETURN">
	<#assign columnlist= columnlist + "{ text: '${StringUtil.wrapString(uiLabelMap.ProductManufactureDate)}', dataField: 'datetimeManufactured', align: 'left', width: 120, columntype: 'datetimeinput', filtertype: 'range', cellsformat: 'dd/MM/yyyy', cellsalign: 'right',
		cellsrenderer: function (row, column, value){
			if (value === null || value === undefined || '' === value){
				return '<span style=\"text-align: right\"></span>';
			}
		}
	},
	{ text: '${StringUtil.wrapString(uiLabelMap.ProductExpireDate)}', dataField: 'expireDate', align: 'left', width: 120, columntype: 'datetimeinput', filtertype: 'range', cellsformat: 'dd/MM/yyyy', cellsalign: 'right',
		cellsrenderer: function (row, column, value){
			if (value === null || value === undefined || '' === value){
				return '<span style=\"text-align: right\"></span>';
			}
		}
	},"/>
	<#elseif returnHeader.returnHeaderTypeId == "VENDOR_RETURN">
	<#assign columnlist= columnlist + "{ text: '${StringUtil.wrapString(uiLabelMap.ProductManufactureDate)}', dataField: 'datetimeManufactured', align: 'left', width: 120, columntype: 'datetimeinput', filtertype: 'range', cellsformat: 'dd/MM/yyyy', cellsalign: 'right',
			cellsrenderer: function (row, column, value){
				if (value === null || value === undefined || '' === value){
					return '<span style=\"text-align: right\"></span>';
				}
			}
		},
		{ text: '${StringUtil.wrapString(uiLabelMap.ProductExpireDate)}', dataField: 'expireDate', align: 'left', width: 120, columntype: 'datetimeinput', filtertype: 'range', cellsformat: 'dd/MM/yyyy', cellsalign: 'right',
			cellsrenderer: function (row, column, value){
				if (value === null || value === undefined || '' === value){
					return '<span style=\"text-align: right\"></span>';
				}
			}
		},"/>
	</#if>	
	<#if returnHeader.returnHeaderTypeId == "CUSTOMER_RETURN">
		<#assign columnlist= columnlist + "{ text: '${uiLabelMap.RequiredNumber}', dataField: 'returnQuantity', width: 130, editable:false, filtertype: 'number',
			cellsrenderer: function (row, column, value){
				return '<span style=\"text-align: right\">' + formatnumber(value) + '</span>';
			}
		},
		"/>
	<#else>
		<#assign columnlist= columnlist + "{ text: '${uiLabelMap.RequiredNumber}', dataField: 'returnQuantity', width: 130, editable:false, filtertype: 'number',
			cellsrenderer: function (row, column, value){
				var data = $('#jqxgridProductReturn').jqxGrid('getrowdata', row);
				if (data.requireAmount && data.requireAmount == 'Y') {
					value = data.returnAmount;
				}
				return '<span style=\"text-align: right\">' + formatnumber(value) + '</span>';
			}
		},
		"/>
	</#if>
	<#if returnHeader.returnHeaderTypeId == "CUSTOMER_RETURN">
	<#assign columnlist= columnlist + "{ text: '${uiLabelMap.ReceivedNumber}', dataField: 'quantityAccepted', width: 150, editable:false, filtertype: 'number',
		cellsrenderer: function (row, column, value){
			if (value){
				return '<span style=\"text-align: right\">' + formatnumber(value) + '</span>';
			} else {
				return '<span style=\"text-align: right\"></span>';
			}
		}
	},
	{ text: '${uiLabelMap.OrderNumber}', dataField: 'actualExportedQuantity', width: 150, editable:false, filtertype: 'number',
		cellsrenderer: function (row, column, value){
			if (value){
				return '<span style=\"text-align: right\">' + formatnumber(value) +  '</span>';
			} else {
				value = 0;
				return '<span style=\"text-align: right\">' + formatnumber(value) +  '</span>';
			}
		}
	},
	{ text: '${uiLabelMap.OrderId}', dataField: 'orderId', width: 150, editable:false,
		cellsrenderer: function (row, column, value){
			if (value === null || value === undefined || '' === value){
				return '<span style=\"text-align: right\"></span>';
			}
		}
	},
	"/>	
	<#elseif returnHeader.returnHeaderTypeId == "VENDOR_RETURN">
		<#if returnHeader.statusId == "SUP_RETURN_SHIPPED" || returnHeader.statusId == "SUP_RETURN_COMPLETED" >
			<#assign columnlist= columnlist + "{ text: '${uiLabelMap.ExportedQuantity}', dataField: 'issuedQuantity', width: 150, editable:false, filtertype: 'number',
				cellsrenderer: function (row, column, value){
					var data = $('#jqxgridProductReturn').jqxGrid('getrowdata', row);
					if (data.requireAmount && data.requireAmount == 'Y') {
						value = data.issuedWeight;
					}
					if (!value){
						if (data.requireAmount && data.requireAmount == 'Y') {
							return '<span style=\"text-align: right\">' + formatnumber(data.returnAmount)+ '</span>';
						} 
						return '<span style=\"text-align: right\">' + formatnumber(data.returnQuantity)+ '</span>';
					} else {
						return '<span style=\"text-align: right\">' + formatnumber(value)+ '</span>';
					}
				}
			},"/>
		</#if>
		<#assign columnlist= columnlist + "{ text: '${uiLabelMap.OrderNumber}', dataField: 'actualDeliveredQuantity', width: 150, editable:false, filtertype: 'number', hidden: true,
		cellsrenderer: function (row, column, value){	
			var data = $('#jqxgridProductReturn').jqxGrid('getrowdata', row);
			if (data.requireAmount && data.requireAmount == 'Y') {
				value = data.actualDeliveredAmount;
			}
			if (value){
				return '<span style=\"text-align: right\">' + formatnumber(value) + '</span>';
			} else {
				value = 0;
				return '<span style=\"text-align: right\">' + formatnumber(value) + '</span>';
			}
		}
	},
	"/>	
	</#if>
	<#if returnHeader.returnHeaderTypeId == "VENDOR_RETURN">
	<#assign columnlist= columnlist + " 
		{ text: '${uiLabelMap.Unit}', datafield: 'quantityUomId', align: 'left', width: 120, filtertype: 'checkedlist',
			cellsrenderer: function (row, column, value){
				var data = $('#jqxgridProductReturn').jqxGrid('getrowdata', row);
				if (data.requireAmount && data.requireAmount == 'Y') {
					value = data.weightUomId;
				}
				return '<span style=\"text-align: right\">' + getUomDescription(value) +'</span>';
			},
			createfilterwidget: function (column, columnElement, widget) {
			var filterDataAdapter = new $.jqx.dataAdapter(uomData, {
				autoBind: true
			});
			var records = filterDataAdapter.records;
			widget.jqxDropDownList({source: records, displayMember: 'uomId', valueMember: 'uomId', dropDownWidth: 'auto', autoDropDownHeight: 'auto',
				renderer: function(index, label, value){
					getUomDescription(value);
				}
			});
			widget.jqxDropDownList('checkAll');
			}
		},">
		<#else>
	<#assign columnlist= columnlist + " 
		{ text: '${uiLabelMap.Unit}', datafield: 'quantityUomId', align: 'left', width: 120, filtertype: 'checkedlist',
			cellsrenderer: function (row, column, value){
				return '<span style=\"text-align: right\">' + getUomDescription(value) +'</span>';
			},
			createfilterwidget: function (column, columnElement, widget) {
				var filterDataAdapter = new $.jqx.dataAdapter(uomData, {
					autoBind: true
				});
				var records = filterDataAdapter.records;
				widget.jqxDropDownList({source: records, displayMember: 'uomId', valueMember: 'uomId', dropDownWidth: 'auto', autoDropDownHeight: 'auto',
					renderer: function(index, label, value){
						getUomDescription(value);
					}
				});
				widget.jqxDropDownList('checkAll');
				}
		}," >
	</#if>
	<#assign columnlist= columnlist + " 
		{ text: '${uiLabelMap.Batch}', dataField: 'lotId', editable:false, width: 100, 
			cellsrenderer: function (row, column, value){
			if(value){
				return '<span style=\"text-align: right\">' + value + '<span>';
			} else {
				return '<span style=\"text-align: right\"><span>';
			}
		}
	},
		{ text: '${uiLabelMap.BLLocationCode}', hidden:true, dataField: 'locationCode', editable:false, width: 100, 
	},
	{ text: '${uiLabelMap.UnitPrice}', dataField: 'returnPrice', width: 150, editable:false, cellsalign: 'right',
		cellsrenderer: function (row, column, value){
			if(value){
				return '<span style=\"text-align: right\">' + formatcurrency(value, '${returnHeader.currencyUomId?if_exists}') + '<span>';
			} else {
				return '<span style=\"text-align: right\"><span>';
			}
		}
	},
	{ text: '${uiLabelMap.Reason}', dataField: 'returnReasonId', width: 200, editable:false,
		cellsrenderer: function (row, column, value){
			if(value){
				return '<span>' + mapReturnReason[value] + '<span>';
			}
		}
	},
	{ text: '${uiLabelMap.ReturnType}', dataField: 'returnTypeId', editable:false, width: 200, 
		cellsrenderer: function (row, column, value){
			if(value){
				return '<span>' + mapReturnType[value] + '<span>';
			}
		}
	},
	 "/>
	<#assign dataField="[{ name: 'orderId', type: 'string' },
	{ name: 'productId', type: 'string' },
	{ name: 'productCode', type: 'string' },
	{ name: 'productName', type: 'string' },
	{ name: 'requireAmount', type: 'string' },
	{ name: 'quantityUomId', type: 'string' },
	{ name: 'weightUomId', type: 'string' },
	{ name: 'returnQuantity', type: 'number' },
	{ name: 'returnAmount', type: 'number' },
	{ name: 'receivedQuantity', type: 'number' },
	{ name: 'receivedAmount', type: 'number' },
	{ name: 'returnPrice', type: 'number' },
	{ name: 'actualExportedQuantity', type: 'number' },
	{ name: 'actualExportedWeight', type: 'number' },
	{ name: 'issuedQuantity', type: 'number' },
	{ name: 'issuedWeight', type: 'number' },
	{ name: 'actualDeliveredQuantity', type: 'number' },
	{ name: 'actualDeliveredWeight', type: 'number' },
	{ name: 'amountOnHandDiff', type: 'number' },
	{ name: 'quantityAccepted', type: 'number' },
	{ name: 'returnReasonId', type: 'string' },
	{ name: 'statusId', type: 'string' },
	{ name: 'lotId', type: 'string' },
	{ name: 'locationCode', type: 'string' },
	{ name: 'locationId', type: 'string' },
	{ name: 'returnTypeId', type: 'string' },
	{ name: 'expiredDate', type: 'date', other: 'Timestamp' },
	{ name: 'expireDate', type: 'date', other: 'Timestamp' },
	{ name: 'datetimeManufactured', type: 'date', other: 'Timestamp' },
	{ name: 'datetimeReceived', type: 'date', other: 'Timestamp' },
	{ name: 'manufacturedDate', type: 'date', other: 'Timestamp' },
	]"/>
	<@jqGrid filtersimplemode="true" id="jqxgridProductReturn" addrefresh="true" usecurrencyfunction="true" dataField=dataField columnlist=columnlist clearfilteringbutton="true" filterable="true"  editable="false" 
		url="jqxGeneralServicer?sname=JQGetListReturnDetail&returnId=${parameters.returnId?if_exists}" 
		customTitleProperties="ListProductReturnItem"
		jqGridMinimumLibEnable="true" bindresize="false" 
	/>
</div>