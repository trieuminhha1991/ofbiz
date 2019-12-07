<script type="text/javascript">
<#assign localeStr = "VI" />
	<#if locale = "en">
<#assign localeStr = "EN" />
	</#if>
	var departmentId = '${parameters.departmentId}';
	var invoiceItemTypeId = '${parameters.invoiceItemTypeId}';
	<#assign invoiceItemTypes = delegator.findList("InvoiceItemType", null, null, null, null, false)>
	var invoiceItemTypeData = new Array();
	<#list invoiceItemTypes as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.description?if_exists)/>
		row['invoiceItemTypeId'] = '${item.invoiceItemTypeId?if_exists}';
		row['parentTypeId'] = '${item.parentTypeId?if_exists}';
		row['description'] = '${description?if_exists}';
		invoiceItemTypeData[${item_index}] = row;
	</#list>
	var now = new Date();
	var curMonth = now.getMonth() + 1;
	var curYear = now.getFullYear();
</script>
<#assign initrowdetails = "function (index, parentElement, gridElement, datarecord) {
	var grid = $($(parentElement).children()[0]);
	var listAccCosts = [];
	jQuery.ajax({
		url: 'getCostsAccByVehicle',
        type: 'POST',
        async: false,
        data: {
        	departmentId: departmentId,
        	vehicleId: datarecord.vehicleId,
        	invoiceItemTypeId: invoiceItemTypeId,
        	year: curYear,
        },
        success: function(res) {
        	var costs = res.listCosts;
        	for (var m = 0; m < costs.length; m++) {
        		var map = {};
        		var temp = costs[m].invoiceDate;
        		map = costs[m];
        		map['month'] = temp.month + 1;
        		listAccCosts.push(map);
        	}
        }
	});
	$(grid).attr('id','jqxgrid'+datarecord.vehicleId+'Detail');
	var costsSource = { 
			 datafields: [
					{name: 'invoiceItemTypeId', type: 'String'},
					{name: 'costPriceActual', type: 'number'},
					{name: 'invoiceDate', type: 'date'},
					{name: 'month', type: 'number'},
		         ],
	             localdata: listAccCosts,
	             updaterow: function (rowid, newdata, commit) {
	            	 commit(true);
//	            	 var totalValue = 0;
//	            	 var rows = grid.jqxGrid('getrows');
//	            	 for(var i=0; i<rows.length; i++){
//	            		 var colValue = 0;
//	            		 if(typeof rows[i].valueCost != 'undefined'){
//	            			 colValue = parseInt(rows[i].valueCost);
//	            		 }
//	            		 
//	            		 totalValue += colValue;
//	            	 }
//		        	 $('#jqxgridCost').jqxGrid('setcellvalue', index, 'costPriceTemporary', totalValue);
	             }
         	}
	var nestedGridAdapter = new $.jqx.dataAdapter(costsSource);
	if (grid != null) {
        grid.jqxGrid({
            source: nestedGridAdapter, width: '98%',
            autoheight: true,
            showtoolbar: true,
            showstatusbar: false,
            columnsheight: 30,
            editable: true,
            altrows: true,
            editmode:'click',
            showheader: true,
            selectionmode:'checkbox',
            showaggregates: true,
            statusbarheight: 47,
            rowsheight: 30,
            theme: 'olbius',
            pageable: false,
            columns: [
				{ text: '${uiLabelMap.CostsType}', datafield: 'invoiceItemTypeId', editable: false, hidden: false, width: '15%', pinned: true,
					cellsrenderer: function (row, column, value){
						for (var i in invoiceItemTypeData){
							if (value == invoiceItemTypeData[i].invoiceItemTypeId){
								return '<span> - '+invoiceItemTypeData[i].description+'</span>';
							} 
						}
					},
					cellbeginedit: function (row, datafield, columntype) {
						var month = 11;
						if (month != curMonth){
							return false;
						} else {
							return true;
						}
				    },
				},
				{ dataField: 'costMonth1', text: '${uiLabelMap.Month} 1', columntype: 'numberinput', filteradble: false, sortable: false, align: 'left', editable: true, width: '10%',
					cellsrenderer: function (row, column, value){
						var data = $('#jqxgrid'+datarecord.vehicleId+'Detail').jqxGrid('getrowdata', row);
						var cost = parseInt(data.costPriceActual);
						if (data.month == 1){
							return '<span>'+cost.toLocaleString('${localeStr}')+'<span>';
						}
					},
					cellbeginedit: function (row, datafield, columntype) {
						var month = 1;
						if (month != curMonth){
							return false;
						} else {
							return true;
						}
				    },
				},
				{ dataField: 'costMonth2', text: '${uiLabelMap.Month} 2', columntype: 'numberinput', filteradble: false, sortable: false, align: 'left', editable: true, width: '10%',
					cellsrenderer: function (row, column, value){
						var data = $('#jqxgrid'+datarecord.vehicleId+'Detail').jqxGrid('getrowdata', row);
						var cost = parseInt(data.costPriceActual);
						if (data.month == 2){
							return '<span>'+cost.toLocaleString('${localeStr}')+'<span>';
						}
					},
					cellbeginedit: function (row, datafield, columntype) {
						var month = 2;
						if (month != curMonth){
							return false;
						} else {
							return true;
						}
				    },
				},
				{ dataField: 'costMonth3', text: '${uiLabelMap.Month} 3', columntype: 'numberinput', filteradble: false, sortable: false, align: 'left', editable: true, width: '10%',
					cellsrenderer: function (row, column, value){
						var data = $('#jqxgrid'+datarecord.vehicleId+'Detail').jqxGrid('getrowdata', row);
						var cost = parseInt(data.costPriceActual);
						if (data.month == 3){
							return '<span>'+cost.toLocaleString('${localeStr}')+'<span>';
						}
					},
					cellbeginedit: function (row, datafield, columntype) {
						var month = 3;
						if (month != curMonth){
							return false;
						} else {
							return true;
						}
				    },
				},
				{ dataField: 'costMonth4', text: '${uiLabelMap.Month} 4', columntype: 'numberinput', filteradble: false, sortable: false, align: 'left', editable: true, width: '10%',
					cellsrenderer: function (row, column, value){
						var data = $('#jqxgrid'+datarecord.vehicleId+'Detail').jqxGrid('getrowdata', row);
						var cost = parseInt(data.costPriceActual);
						if (data.month == 4){
							return '<span>'+cost.toLocaleString('${localeStr}')+'<span>';
						}
					},
					cellbeginedit: function (row, datafield, columntype) {
						var month = 4;
						if (month != curMonth){
							return false;
						} else {
							return true;
						}
				    },
				},
				{ dataField: 'costMonth5', text: '${uiLabelMap.Month} 5', columntype: 'numberinput', filteradble: false, sortable: false, align: 'left', editable: true, width: '10%',
					cellsrenderer: function (row, column, value){
						var data = $('#jqxgrid'+datarecord.vehicleId+'Detail').jqxGrid('getrowdata', row);
						var cost = parseInt(data.costPriceActual);
						if (data.month == 5){
							return '<span>'+cost.toLocaleString('${localeStr}')+'<span>';
						}
					},
					cellbeginedit: function (row, datafield, columntype) {
						var month = 5;
						if (month != curMonth){
							return false;
						} else {
							return true;
						}
				    },
				},
				{ dataField: 'costMonth6', text: '${uiLabelMap.Month} 6', columntype: 'numberinput', filteradble: false, sortable: false, align: 'left', editable: true, width: '10%',
					cellsrenderer: function (row, column, value){
						var data = $('#jqxgrid'+datarecord.vehicleId+'Detail').jqxGrid('getrowdata', row);
						var cost = parseInt(data.costPriceActual);
						if (data.month == 6){
							return '<span>'+cost.toLocaleString('${localeStr}')+'<span>';
						}
					},
					cellbeginedit: function (row, datafield, columntype) {
						var month = 6;
						if (month != curMonth){
							return false;
						} else {
							return true;
						}
				    },
				},
				{ dataField: 'costMonth7', text: '${uiLabelMap.Month} 7', columntype: 'numberinput', filteradble: false, sortable: false, align: 'left', editable: true, width: '10%',
					cellsrenderer: function (row, column, value){
						var data = $('#jqxgrid'+datarecord.vehicleId+'Detail').jqxGrid('getrowdata', row);
						var cost = parseInt(data.costPriceActual);
						if (data.month == 7){
							return '<span>'+cost.toLocaleString('${localeStr}')+'<span>';
						}
					},
					cellbeginedit: function (row, datafield, columntype) {
						var month = 7;
						if (month != curMonth){
							return false;
						} else {
							return true;
						}
				    },
				},
				{ dataField: 'costMonth8', text: '${uiLabelMap.Month} 8', columntype: 'numberinput', filteradble: false, sortable: false, align: 'left', editable: true, width: '10%',
					cellsrenderer: function (row, column, value){
						var data = $('#jqxgrid'+datarecord.vehicleId+'Detail').jqxGrid('getrowdata', row);
						var cost = parseInt(data.costPriceActual);
						if (data.month == 8){
							return '<span>'+cost.toLocaleString('${localeStr}')+'<span>';
						}
					},
					cellbeginedit: function (row, datafield, columntype) {
						var month = 8;
						if (month != curMonth){
							return false;
						} else {
							return true;
						}
				    },
				},
				{ dataField: 'costMonth9', text: '${uiLabelMap.Month} 9', columntype: 'numberinput', filteradble: false, sortable: false, align: 'left', editable: true, width: '10%',
					cellsrenderer: function (row, column, value){
						var data = $('#jqxgrid'+datarecord.vehicleId+'Detail').jqxGrid('getrowdata', row);
						var cost = parseInt(data.costPriceActual);
						if (data.month == 9){
							return '<span>'+cost.toLocaleString('${localeStr}')+'<span>';
						}
					},
					cellbeginedit: function (row, datafield, columntype) {
						var month = 9;
						if (month != curMonth){
							return false;
						} else {
							return true;
						}
				    },
				},
				{ dataField: 'costMonth10', text: '${uiLabelMap.Month} 10', columntype: 'numberinput', filteradble: false, sortable: false, align: 'left', editable: true, width: '10%',
					cellsrenderer: function (row, column, value){
						var data = $('#jqxgrid'+datarecord.vehicleId+'Detail').jqxGrid('getrowdata', row);
						var cost = parseInt(data.costPriceActual);
						if (data.month == 10){
							return '<span>'+cost.toLocaleString('${localeStr}')+'<span>';
						}
					},
					cellbeginedit: function (row, datafield, columntype) {
						var month = 10;
						if (month != curMonth){
							return false;
						} else {
							return true;
						}
				    },
				},
				{ dataField: 'costMonth11', text: '${uiLabelMap.Month} 11', columntype: 'numberinput', filteradble: false, sortable: false, align: 'left', editable: true, width: '10%',
					cellsrenderer: function (row, column, value){
						var data = $('#jqxgrid'+datarecord.vehicleId+'Detail').jqxGrid('getrowdata', row);
						var cost = parseInt(data.costPriceActual);
						if (data.month == 11){
							return '<span>'+cost.toLocaleString('${localeStr}')+'<span>';
						}
					},
					cellbeginedit: function (row, datafield, columntype) {
						var month = 11;
						if (month != curMonth){
							return false;
						} else {
							return true;
						}
				    },
				},
				{ dataField: 'costMonth12', text: '${uiLabelMap.Month} 12', columntype: 'numberinput', filteradble: false, sortable: false, align: 'left', editable: true, width: '10%',
					cellsrenderer: function (row, column, value){
						var data = $('#jqxgrid'+datarecord.vehicleId+'Detail').jqxGrid('getrowdata', row);
						if (data.month == 12){
							var cost = parseInt(data.costPriceActual);
							return '<span>'+cost.toLocaleString('${localeStr}')+'<span>';
						}
					},
					cellbeginedit: function (row, datafield, columntype) {
						var month = 12;
						if (month != curMonth){
							return false;
						} else {
							return true;
						}
				    },
				},
			 ]
        });
	}
}
"/>

<#assign dataFieldVehicle = "[
	{name: 'vehicleId', type: 'String'},
	{name: 'vehicleName', type: 'String'},
	{name: 'partyCarrierId', type: 'String'},
	{name: 'maxWeight', type: 'number'},
	{name: 'minWeight', type: 'number'},
	{name: 'weightUomId', type: 'String'},
	{name: 'statusId', type: 'statusId'},
]">

<#assign columnListVehicle ="
	{ dataField: 'vehicleId', text: '${uiLabelMap.NumberPlate}', filteradble: false, sortable: false, align: 'left', editable: false, width: '20%', pinned: true,
		cellsrenderer: function(row, column, value){
			var data = $('#jqxgridVehicle').jqxGrid('getrowdata', row);
		},
	},
	{ dataField: 'vehicleName', text: '${uiLabelMap.VehicleName}', filteradble: false, sortable: false, align: 'left', editable: false, width: '20%', 
		cellsrenderer: function(row, column, value){
			var data = $('#jqxgridVehicle').jqxGrid('getrowdata', row);
		},
	},
	{ dataField: 'partyCarrierId', text: '${uiLabelMap.Owner}', filteradble: false, sortable: false, align: 'left', editable: false, width: '20%', 
		cellsrenderer: function(row, column, value){
			var data = $('#jqxgridVehicle').jqxGrid('getrowdata', row);
		},
	},
	{ dataField: 'minWeight', text: '${uiLabelMap.minWeight}', filteradble: false, sortable: false, align: 'left', editable: false, width: '20%', 
		cellsrenderer: function(row, column, value){
			var data = $('#jqxgridVehicle').jqxGrid('getrowdata', row);
		},
	},
	{ dataField: 'maxWeight', text: '${uiLabelMap.maxWeight}', filteradble: false, sortable: false, align: 'left', editable: false, width: '20%', 
		cellsrenderer: function(row, column, value){
			var data = $('#jqxgridVehicle').jqxGrid('getrowdata', row);
		},
	},
	{ dataField: 'weightUomId', text: '${uiLabelMap.WeightUomId}', filteradble: false, sortable: false, align: 'left', editable: false, width: '20%', 
		cellsrenderer: function(row, column, value){
			var data = $('#jqxgridVehicle').jqxGrid('getrowdata', row);
		},
	},
	{ dataField: 'statusId', text: '${uiLabelMap.CommonStatus}', filteradble: false, sortable: false, align: 'left', editable: false, width: '20%', 
		cellsrenderer: function(row, column, value){
			var data = $('#jqxgridVehicle').jqxGrid('getrowdata', row);
		},
	},
"/>

<@jqGrid id="jqxgridVehicle" filterable="true" viewSize="10" filtersimplemode="true" addType="popup" dataField=dataFieldVehicle columnlist=columnListVehicle clearfilteringbutton="true" columngrouplist=""
	showtoolbar="true" addrow="false" deleterow="false" alternativeAddPopup="" editable="true" editmode="click" initrowdetails="true" initrowdetailsDetail=initrowdetails
	url="jqxGeneralServicer?sname=JQGetListVehicles" 
/>