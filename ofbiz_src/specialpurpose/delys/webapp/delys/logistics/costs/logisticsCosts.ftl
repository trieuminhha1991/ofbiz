<script type="text/javascript">
<#assign localeStr = "VI" />
	<#if locale = "en">
<#assign localeStr = "EN" />
	</#if>
	var groupNameDlv = null;
	var groupNameWh = null;
	var departmentId = '${parameters.departmentId?if_exists}';
	<#assign warehouseGr = delegator.findOne("PartyGroup", {"partyId", "LOG_WAREHOUSE"}, false)>
	groupNameWh = '${warehouseGr.groupNameLocal}';
	<#assign deliveryGr = delegator.findOne("PartyGroup", {"partyId", "LOG_DELIVERY"}, false)>
	groupNameDlv = '${deliveryGr.groupNameLocal}';
	<#assign types = delegator.findList("CostAccBaseGroupByInvoiceItemType", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("departmentId", Static["org.ofbiz.entity.condition.EntityJoinOperator"].IN, Static["org.ofbiz.base.util.UtilMisc"].toList("LOG_WAREHOUSE", "LOG_DELIVERY", "LOGISTICS")), null, null, null, false)>
	var invoiceItemTypeData = new Array();
	<#list types as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.description?if_exists)/>
		row['invoiceItemTypeId'] = '${item.invoiceItemTypeId?if_exists}';
		row['parentTypeId'] = '${item.parentTypeId?if_exists}';
		row['description'] = '${description?if_exists}';
		invoiceItemTypeData[${item_index}] = row;
	</#list>
	
	<#assign types = delegator.findList("InvoiceItemType", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("parentTypeId", "LOGISTICS_COST"), null, null, null, false)>
	var parentTypeData = new Array();
	<#if parameters.departmentId?has_content && parameters.departmentId == "LOG_WAREHOUSE">
		<#list types as item>
			<#assign base = delegator.findList("CostAccBaseGroupByInvoiceItemType", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("invoiceItemTypeId", item.invoiceItemTypeId ,"departmentId", "LOG_WAREHOUSE")), null, null, null, false)>
			<#if base?has_content>
				var row = {};
				<#assign description = StringUtil.wrapString(item.get('description', local))/>
				row['invoiceItemTypeId'] = '${item.invoiceItemTypeId?if_exists}';
				row['parentTypeId'] = '${item.parentTypeId?if_exists}';
				row['description'] = '${description?if_exists}';
				parentTypeData[${item_index}] = row;
			</#if>
		</#list>
	<#elseif parameters.departmentId?has_content && parameters.departmentId == "LOG_DELIVERY">
		<#list types as item>
			<#assign base = delegator.findList("CostAccBaseGroupByInvoiceItemType", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("invoiceItemTypeId", item.invoiceItemTypeId ,"departmentId", "LOG_DELIVERY")), null, null, null, false)>
			<#if base?has_content>
				var row = {};
				<#assign description = StringUtil.wrapString(item.get('description', local))/>
				row['invoiceItemTypeId'] = '${item.invoiceItemTypeId?if_exists}';
				row['parentTypeId'] = '${item.parentTypeId?if_exists}';
				row['description'] = '${description?if_exists}';
				parentTypeData[${item_index}] = row;
			</#if>
		</#list>
	<#else>
		<#list types as item>
			var row = {};
			<#assign description = StringUtil.wrapString(item.get('description', local))/>
			row['invoiceItemTypeId'] = '${item.invoiceItemTypeId?if_exists}';
			row['parentTypeId'] = '${item.parentTypeId?if_exists}';
			row['description'] = '${description?if_exists}';
			parentTypeData[${item_index}] = row;
		</#list>
	</#if>
	var now = new Date();
	var curMonth = now.getMonth() + 1;
	var curYear = now.getFullYear();
	
	<#assign currencyUoms = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", "CURRENCY_MEASURE"), null, null, null, false)>
	var currencyUomData = new Array();
	<#list currencyUoms as item>
		var row = {};
		<#assign abbreviation = StringUtil.wrapString(item.abbreviation?if_exists)/>
		row['currencyUomId'] = '${item.uomId?if_exists}';
		row['description'] = '${abbreviation?if_exists}';
		currencyUomData[${item_index}] = row;
	</#list>
</script>

<#assign dataField = "[
	{name: 'departmentId', type: 'String'},
	{name: 'invoiceItemTypeId', type: 'String'},
	{name: 'costMonth1', type: 'number'},
	{name: 'costMonth2', type: 'number'},
	{name: 'costMonth3', type: 'number'},
	{name: 'costMonth4', type: 'number'},
	{name: 'costMonth5', type: 'number'},
	{name: 'costMonth6', type: 'number'},
	{name: 'costMonth7', type: 'number'},
	{name: 'costMonth8', type: 'number'},
	{name: 'costMonth9', type: 'number'},
	{name: 'costMonth10', type: 'number'},
	{name: 'costMonth11', type: 'number'},
	{name: 'costMonth12', type: 'number'},
	{name: 'isParent', type: 'String'},
	{name: 'isMulti', type: 'String'},
]">

<#assign columnList ="
	{dataField: 'departmentId', text: '${uiLabelMap.DepartmentId}', filteradble: false, sortable: false, align: 'left', editable: false, width: '5%', 
		cellsrenderer: function(row, column, value){
			if (value == 'LOG_DELIVERY'){
				return '<span>'+groupNameDlv+'</span>';
			} else if (value == 'LOG_WAREHOUSE'){
				return '<span>'+groupNameWh+'</span>';
			}
		},
		cellclassname: function (row, column, value, data) {
			var data = $('#jqxgridCosts').jqxGrid('getrowdata', row);
			var checkParent = data.isParent;
			var checkMulti = data.isMulti;
			if (checkParent || checkMulti){
				return 'background-bisque';
			}
		},
	},
//	{dataField: 'groupId', text: '${uiLabelMap.GroupId}', filteradble: false, sortable: false, align: 'left', editable: false, width: '7%',
//		cellsrenderer: function(row, column, value){
//			
//		},
//		cellclassname: function (row, column, value, data) {
//			var data = $('#jqxgridCosts').jqxGrid('getrowdata', row);
//			var checkParent = data.isParent;
//			var checkMulti = data.isMulti;
//			if (checkParent || checkMulti){
//				return 'background-bisque';
//			}
//		},
//	},
	{dataField: 'invoiceItemTypeId', text: '${uiLabelMap.LogCostsType}', filteradble: false, sortable: false, align: 'left', editable: false, width: '15%',
		cellsrenderer: function(row, column, value){
			var data = $('#jqxgridCosts').jqxGrid('getrowdata', row);
			var checkParent = data.isParent;
			var checkMulti = data.isMulti;
			for (var i in invoiceItemTypeData){
				if (value == invoiceItemTypeData[i].invoiceItemTypeId){
					if (!checkParent){
						if (checkMulti){
							return '<span style=\"font-weight: bold;\">'+invoiceItemTypeData[i].description+'</span>';
						} else {
							return '<span> - '+invoiceItemTypeData[i].description+'</span>';
						}
					} else {
						return '<span style=\"font-weight: bold;\">'+invoiceItemTypeData[i].description+'</span>';
					}
				} 
			}
		},
		cellclassname: function (row, column, value, data) {
			var data = $('#jqxgridCosts').jqxGrid('getrowdata', row);
			var checkParent = data.isParent;
			var checkMulti = data.isMulti;
			if (checkParent || checkMulti){
				return 'background-bisque';
			}
		},
	},
	{dataField: 'costMonth1', text: '${uiLabelMap.Month} 1', columntype: 'numberinput', filteradble: false, sortable: false, align: 'left', editable: true, width: '10%', 
		cellsrenderer: function(row, column, value){
			var data = $('#jqxgridCosts').jqxGrid('getrowdata', row);
			var checkParent = data.isParent;
			var checkMulti = data.isMulti;
			var localeQuantity = parseInt(value);
			if (checkParent){
				return '<span style=\"font-weight: bold;\" title=' + localeQuantity.toLocaleString('${localeStr}') + '>' + localeQuantity.toLocaleString('${localeStr}') + '</span>';
			} else {
				if (checkMulti){
					return '<span style=\"font-weight: bold;\" title=' + localeQuantity.toLocaleString('${localeStr}') + '>' + localeQuantity.toLocaleString('${localeStr}') + '</span>';
				} else {
					return '<span title=' + localeQuantity.toLocaleString('${localeStr}') + '>' + localeQuantity.toLocaleString('${localeStr}') + '</span>';
				}
			}
		},
		cellclassname: function (row, column, value, data) {
			var data = $('#jqxgridCosts').jqxGrid('getrowdata', row);
			var checkParent = data.isParent;
			var checkMulti = data.isMulti;
			if (checkParent || checkMulti){
				return 'background-bisque';
			}
		},
		cellbeginedit: function (row, datafield, columntype) {
			var month = 1;
			var data = $('#jqxgridCosts').jqxGrid('getrowdata', row);
			var checkParent = data.isParent;
			var checkMulti = data.isMulti;
			if (checkParent || month != curMonth || checkMulti){
				return false;
			} else {
				return true;
			}
	    },
	},
	{dataField: 'costMonth2', text: '${uiLabelMap.Month} 2', columntype: 'numberinput', filteradble: false, sortable: false, align: 'left', editable: true, width: '10%', 
		cellsrenderer: function(row, column, value){
			var data = $('#jqxgridCosts').jqxGrid('getrowdata', row);
			var checkParent = data.isParent;
			var checkMulti = data.isMulti;
			var localeQuantity = parseInt(value);
			if (checkParent){
				return '<span style=\"font-weight: bold;\" title=' + localeQuantity.toLocaleString('${localeStr}') + '>' + localeQuantity.toLocaleString('${localeStr}') + '</span>';
			} else {
				if (checkMulti){
					return '<span style=\"font-weight: bold;\" title=' + localeQuantity.toLocaleString('${localeStr}') + '>' + localeQuantity.toLocaleString('${localeStr}') + '</span>';
				} else {
					return '<span title=' + localeQuantity.toLocaleString('${localeStr}') + '>' + localeQuantity.toLocaleString('${localeStr}') + '</span>';
				}
			}
		},
		cellclassname: function (row, column, value, data) {
			var data = $('#jqxgridCosts').jqxGrid('getrowdata', row);
			var checkParent = data.isParent;
			var checkMulti = data.isMulti;
			if (checkParent || checkMulti){
				return 'background-bisque';
			}
		},
		cellbeginedit: function (row, datafield, columntype) {
			var month = 2;
			var data = $('#jqxgridCosts').jqxGrid('getrowdata', row);
			var checkParent = data.isParent;
			var checkMulti = data.isMulti;
			if (checkParent || month != curMonth || checkMulti){
				return false;
			} else {
				return true;
			}
	    },
	},
	{dataField: 'costMonth3', text: '${uiLabelMap.Month} 3', columntype: 'numberinput', filteradble: false, sortable: false, align: 'left', editable: true, width: '10%', 
		cellsrenderer: function(row, column, value){
			var data = $('#jqxgridCosts').jqxGrid('getrowdata', row);
			var checkParent = data.isParent;
			var checkMulti = data.isMulti;
			var localeQuantity = parseInt(value);
			if (checkParent){
				return '<span style=\"font-weight: bold;\" title=' + localeQuantity.toLocaleString('${localeStr}') + '>' + localeQuantity.toLocaleString('${localeStr}') + '</span>';
			} else {
				if (checkMulti){
					return '<span style=\"font-weight: bold;\" title=' + localeQuantity.toLocaleString('${localeStr}') + '>' + localeQuantity.toLocaleString('${localeStr}') + '</span>';
				} else {
					return '<span title=' + localeQuantity.toLocaleString('${localeStr}') + '>' + localeQuantity.toLocaleString('${localeStr}') + '</span>';
				}
			}
		},
		cellclassname: function (row, column, value, data) {
			var data = $('#jqxgridCosts').jqxGrid('getrowdata', row);
			var checkParent = data.isParent;
			var checkMulti = data.isMulti;
			if (checkParent || checkMulti){
				return 'background-bisque';
			}
		},
		cellbeginedit: function (row, datafield, columntype) {
			var month = 3;
			var data = $('#jqxgridCosts').jqxGrid('getrowdata', row);
			var checkParent = data.isParent;
			var checkMulti = data.isMulti;
			if (checkParent || month != curMonth || checkMulti){
				return false;
			} else {
				return true;
			}
	    },
	},
	{dataField: 'costMonth4', text: '${uiLabelMap.Month} 4', columntype: 'numberinput', filteradble: false, sortable: false, align: 'left', editable: true, width: '10%', 
		cellsrenderer: function(row, column, value){
			var data = $('#jqxgridCosts').jqxGrid('getrowdata', row);
			var checkParent = data.isParent;
			var checkMulti = data.isMulti;
			var localeQuantity = parseInt(value);
			if (checkParent){
				return '<span style=\"font-weight: bold;\" title=' + localeQuantity.toLocaleString('${localeStr}') + '>' + localeQuantity.toLocaleString('${localeStr}') + '</span>';
			} else {
				if (checkMulti){
					return '<span style=\"font-weight: bold;\" title=' + localeQuantity.toLocaleString('${localeStr}') + '>' + localeQuantity.toLocaleString('${localeStr}') + '</span>';
				} else {
					return '<span title=' + localeQuantity.toLocaleString('${localeStr}') + '>' + localeQuantity.toLocaleString('${localeStr}') + '</span>';
				}
			}
		},
		cellclassname: function (row, column, value, data) {
			var data = $('#jqxgridCosts').jqxGrid('getrowdata', row);
			var checkParent = data.isParent;
			var checkMulti = data.isMulti;
			if (checkParent || checkMulti){
				return 'background-bisque';
			}
		},
		cellbeginedit: function (row, datafield, columntype) {
			var month = 4;
			var data = $('#jqxgridCosts').jqxGrid('getrowdata', row);
			var checkParent = data.isParent;
			var checkMulti = data.isMulti;
			if (checkParent || month != curMonth || checkMulti){
				return false;
			} else {
				return true;
			}
	    },
	},
	{dataField: 'costMonth5', text: '${uiLabelMap.Month} 5', columntype: 'numberinput', filteradble: false, sortable: false, align: 'left', editable: true, width: '10%', 
		cellsrenderer: function(row, column, value){
			var data = $('#jqxgridCosts').jqxGrid('getrowdata', row);
			var checkParent = data.isParent;
			var checkMulti = data.isMulti;
			var localeQuantity = parseInt(value);
			if (checkParent){
				return '<span style=\"font-weight: bold;\" title=' + localeQuantity.toLocaleString('${localeStr}') + '>' + localeQuantity.toLocaleString('${localeStr}') + '</span>';
			} else {
				if (checkMulti){
					return '<span style=\"font-weight: bold;\" title=' + localeQuantity.toLocaleString('${localeStr}') + '>' + localeQuantity.toLocaleString('${localeStr}') + '</span>';
				} else {
					return '<span title=' + localeQuantity.toLocaleString('${localeStr}') + '>' + localeQuantity.toLocaleString('${localeStr}') + '</span>';
				}
			}
		},
		cellclassname: function (row, column, value, data) {
			var data = $('#jqxgridCosts').jqxGrid('getrowdata', row);
			var checkParent = data.isParent;
			var checkMulti = data.isMulti;
			if (checkParent || checkMulti){
				return 'background-bisque';
			}
		},
		cellbeginedit: function (row, datafield, columntype) {
			var month = 5;
			var data = $('#jqxgridCosts').jqxGrid('getrowdata', row);
			var checkParent = data.isParent;
			var checkMulti = data.isMulti;
			if (checkParent || month != curMonth || checkMulti){
				return false;
			} else {
				return true;
			}
	    },
	},
	{dataField: 'costMonth6', text: '${uiLabelMap.Month} 6', columntype: 'numberinput', filteradble: false, sortable: false, align: 'left', editable: true, width: '10%', 
		cellsrenderer: function(row, column, value){
			var data = $('#jqxgridCosts').jqxGrid('getrowdata', row);
			var checkParent = data.isParent;
			var checkMulti = data.isMulti;
			var localeQuantity = parseInt(value);
			if (checkParent){
				return '<span style=\"font-weight: bold;\" title=' + localeQuantity.toLocaleString('${localeStr}') + '>' + localeQuantity.toLocaleString('${localeStr}') + '</span>';
			} else {
				if (checkMulti){
					return '<span style=\"font-weight: bold;\" title=' + localeQuantity.toLocaleString('${localeStr}') + '>' + localeQuantity.toLocaleString('${localeStr}') + '</span>';
				} else {
					return '<span title=' + localeQuantity.toLocaleString('${localeStr}') + '>' + localeQuantity.toLocaleString('${localeStr}') + '</span>';
				}
			}
		},
		cellclassname: function (row, column, value, data) {
			var checkParent = false;
			var data = $('#jqxgridCosts').jqxGrid('getrowdata', row);
			var checkParent = data.isParent;
			var checkMulti = data.isMulti;
			if (checkParent || checkMulti){
				return 'background-bisque';
			}
		},
		cellbeginedit: function (row, datafield, columntype) {
			var month = 6;
			var data = $('#jqxgridCosts').jqxGrid('getrowdata', row);
			var checkParent = data.isParent;
			var checkMulti = data.isMulti;
			if (checkParent || month != curMonth || checkMulti){
				return false;
			} else {
				return true;
			}
	    },
	},
	{dataField: 'costMonth7', text: '${uiLabelMap.Month} 7', columntype: 'numberinput', filteradble: false, sortable: false, align: 'left', editable: true, width: '10%', 
		cellsrenderer: function(row, column, value){
			var data = $('#jqxgridCosts').jqxGrid('getrowdata', row);
			var checkParent = data.isParent;
			var checkMulti = data.isMulti;
			var localeQuantity = parseInt(value);
			if (checkParent){
				return '<span style=\"font-weight: bold;\" title=' + localeQuantity.toLocaleString('${localeStr}') + '>' + localeQuantity.toLocaleString('${localeStr}') + '</span>';
			} else {
				if (checkMulti){
					return '<span style=\"font-weight: bold;\" title=' + localeQuantity.toLocaleString('${localeStr}') + '>' + localeQuantity.toLocaleString('${localeStr}') + '</span>';
				} else {
					return '<span title=' + localeQuantity.toLocaleString('${localeStr}') + '>' + localeQuantity.toLocaleString('${localeStr}') + '</span>';
				}
			}
		},
		cellclassname: function (row, column, value, data) {
			var data = $('#jqxgridCosts').jqxGrid('getrowdata', row);
			var checkParent = data.isParent;
			var checkMulti = data.isMulti;
			if (checkParent || checkMulti){
				return 'background-bisque';
			}
		},
		cellbeginedit: function (row, datafield, columntype) {
			var month = 7;
			var data = $('#jqxgridCosts').jqxGrid('getrowdata', row);
			var checkParent = data.isParent;
			var checkMulti = data.isMulti;
			if (checkParent || month != curMonth || checkMulti){
				return false;
			} else {
				return true;
			}
	    },
	},
	{dataField: 'costMonth8', text: '${uiLabelMap.Month} 8', columntype: 'numberinput', filteradble: false, sortable: false, align: 'left', editable: true, width: '10%', 
		cellsrenderer: function(row, column, value){
			var data = $('#jqxgridCosts').jqxGrid('getrowdata', row);
			var checkParent = data.isParent;
			var checkMulti = data.isMulti;
			var localeQuantity = parseInt(value);
			if (checkParent){
				return '<span style=\"font-weight: bold;\" title=' + localeQuantity.toLocaleString('${localeStr}') + '>' + localeQuantity.toLocaleString('${localeStr}') + '</span>';
			} else {
				if (checkMulti){
					return '<span style=\"font-weight: bold;\" title=' + localeQuantity.toLocaleString('${localeStr}') + '>' + localeQuantity.toLocaleString('${localeStr}') + '</span>';
				} else {
					return '<span title=' + localeQuantity.toLocaleString('${localeStr}') + '>' + localeQuantity.toLocaleString('${localeStr}') + '</span>';
				}
			}
		},
		cellclassname: function (row, column, value, data) {
			var data = $('#jqxgridCosts').jqxGrid('getrowdata', row);
			var checkParent = data.isParent;
			var checkMulti = data.isMulti;
			if (checkParent || checkMulti){
				return 'background-bisque';
			}
		},
		cellbeginedit: function (row, datafield, columntype) {
			var month = 8;
			var data = $('#jqxgridCosts').jqxGrid('getrowdata', row);
			var checkParent = data.isParent;
			var checkMulti = data.isMulti;
			if (checkParent || month != curMonth || checkMulti){
				return false;
			} else {
				return true;
			}
	    },
	},
	{dataField: 'costMonth9', text: '${uiLabelMap.Month} 9', columntype: 'numberinput', filteradble: false, sortable: false, align: 'left', editable: true, width: '10%', 
		cellsrenderer: function(row, column, value){
			var data = $('#jqxgridCosts').jqxGrid('getrowdata', row);
			var checkParent = data.isParent;
			var checkMulti = data.isMulti;
			var localeQuantity = parseInt(value);
			if (checkParent){
				return '<span style=\"font-weight: bold;\" title=' + localeQuantity.toLocaleString('${localeStr}') + '>' + localeQuantity.toLocaleString('${localeStr}') + '</span>';
			} else {
				if (checkMulti){
					return '<span style=\"font-weight: bold;\" title=' + localeQuantity.toLocaleString('${localeStr}') + '>' + localeQuantity.toLocaleString('${localeStr}') + '</span>';
				} else {
					return '<span title=' + localeQuantity.toLocaleString('${localeStr}') + '>' + localeQuantity.toLocaleString('${localeStr}') + '</span>';
				}
			}
		},
		cellbeginedit: function (row, datafield, columntype) {
			var month = 9;
			var data = $('#jqxgridCosts').jqxGrid('getrowdata', row);
			var checkParent = data.isParent;
			var checkMulti = data.isMulti;
			if (checkParent || month != curMonth || checkMulti){
				return false;
			} else {
				return true;
			}
	    },
		cellclassname: function (row, column, value, data) {
			var data = $('#jqxgridCosts').jqxGrid('getrowdata', row);
			var checkParent = data.isParent;
			var checkMulti = data.isMulti;
			if (checkParent || checkMulti){
				return 'background-bisque';
			}
		},
	},
	{dataField: 'costMonth10', text: '${uiLabelMap.Month} 10', columntype: 'numberinput', filteradble: false, sortable: false, align: 'left', editable: true, width: '10%', 
		cellsrenderer: function(row, column, value){
			var data = $('#jqxgridCosts').jqxGrid('getrowdata', row);
			var checkParent = data.isParent;
			var checkMulti = data.isMulti;
			var localeQuantity = parseInt(value);
			if (checkParent){
				return '<span style=\"font-weight: bold;\" title=' + localeQuantity.toLocaleString('${localeStr}') + '>' + localeQuantity.toLocaleString('${localeStr}') + '</span>';
			} else {
				if (checkMulti){
					return '<span style=\"font-weight: bold;\" title=' + localeQuantity.toLocaleString('${localeStr}') + '>' + localeQuantity.toLocaleString('${localeStr}') + '</span>';
				} else {
					return '<span title=' + localeQuantity.toLocaleString('${localeStr}') + '>' + localeQuantity.toLocaleString('${localeStr}') + '</span>';
				}
			}
		},
		cellclassname: function (row, column, value, data) {
			var data = $('#jqxgridCosts').jqxGrid('getrowdata', row);
			var checkParent = data.isParent;
			var checkMulti = data.isMulti;
			if (checkParent || checkMulti){
				return 'background-bisque';
			}
		},
		cellbeginedit: function (row, datafield, columntype) {
			var month = 10;
			var data = $('#jqxgridCosts').jqxGrid('getrowdata', row);
			var checkParent = data.isParent;
			var checkMulti = data.isMulti;
			if (checkParent || month != curMonth || checkMulti){
				return false;
			} else {
				return true;
			}
	    },
	},
	{dataField: 'costMonth11', text: '${uiLabelMap.Month} 11', columntype: 'numberinput', filteradble: false, sortable: false, align: 'left', editable: true, width: '10%', 
		cellsrenderer: function(row, column, value){
			var data = $('#jqxgridCosts').jqxGrid('getrowdata', row);
			var checkParent = data.isParent;
			var checkMulti = data.isMulti;
			var localeQuantity = parseInt(value);
			if (checkParent){
				return '<span style=\"font-weight: bold;\" title=' + localeQuantity.toLocaleString('${localeStr}') + '>' + localeQuantity.toLocaleString('${localeStr}') + '</span>';
			} else {
				if (checkMulti){
					return '<span style=\"font-weight: bold;\" title=' + localeQuantity.toLocaleString('${localeStr}') + '>' + localeQuantity.toLocaleString('${localeStr}') + '</span>';
				} else {
					return '<span title=' + localeQuantity.toLocaleString('${localeStr}') + '>' + localeQuantity.toLocaleString('${localeStr}') + '</span>';
				}
			}
		},
		cellclassname: function (row, column, value, data) {
			var data = $('#jqxgridCosts').jqxGrid('getrowdata', row);
			var checkParent = data.isParent;
			var checkMulti = data.isMulti;
			if (checkParent || checkMulti){
				return 'background-bisque';
			}
		},
		cellbeginedit: function (row, datafield, columntype) {
			var month = 11;
			var data = $('#jqxgridCosts').jqxGrid('getrowdata', row);
			var checkParent = data.isParent;
			var checkMulti = data.isMulti;
			if (checkParent || month != curMonth || checkMulti){
				return false;
			} else {
				return true;
			}
	    },
	},
	{dataField: 'costMonth12', text: '${uiLabelMap.Month} 12', columntype: 'numberinput', filteradble: false, sortable: false, align: 'left', editable: true, width: '10%',
		cellsrenderer: function(row, column, value){
			var data = $('#jqxgridCosts').jqxGrid('getrowdata', row);
			var checkParent = data.isParent;
			var checkMulti = data.isMulti;
			var localeQuantity = parseInt(value);
			if (checkParent){
				return '<span style=\"font-weight: bold;\" title=' + localeQuantity.toLocaleString('${localeStr}') + '>' + localeQuantity.toLocaleString('${localeStr}') + '</span>';
			} else {
				if (checkMulti){
					return '<span style=\"font-weight: bold;\" title=' + localeQuantity.toLocaleString('${localeStr}') + '>' + localeQuantity.toLocaleString('${localeStr}') + '</span>';
				} else {
					return '<span title=' + localeQuantity.toLocaleString('${localeStr}') + '>' + localeQuantity.toLocaleString('${localeStr}') + '</span>';
				}
			}
		},
		cellclassname: function (row, column, value, data) {
			var data = $('#jqxgridCosts').jqxGrid('getrowdata', row);
			var checkParent = data.isParent;
			var checkMulti = data.isMulti;
			if (checkParent || checkMulti){
				return 'background-bisque';
			}
		},
		cellbeginedit: function (row, datafield, columntype) {
			var month = 12;
			var data = $('#jqxgridCosts').jqxGrid('getrowdata', row);
			var checkParent = data.isParent;
			var checkMulti = data.isMulti;
			if (checkParent || month != curMonth || checkMulti){
				return false;
			} else {
				return true;
			}
	    },
	},
"/>
<div>
	<#if security.hasPermission("DLV_COSTS_UPDATE", session) || security.hasPermission("WH_COSTS_UPDATE", session)>
		<#if parameters.invoiceItemTypeId?has_content>
			<@jqGrid id="jqxgridCosts" filterable="true" viewSize="20" filtersimplemode="true" addType="popup" dataField=dataField columnlist=columnList clearfilteringbutton="true" columngrouplist=""
				showtoolbar="true" addrow="true" deleterow="false" alternativeAddPopup="alterpopupWindow" editable="true" selectionmode="checkbox" editmode="click"
				url="jqxGeneralServicer?sname=getCostsByAccBase&departmentId=${parameters.departmentId?if_exists}&invoiceItemTypeId=${parameters.invoiceItemTypeId?if_exists}" 
				otherParams="isParent,isMulti,costMonth1,costMonth2,costMonth3,costMonth4,costMonth5,costMonth6,costMonth7,costMonth8,costMonth9,costMonth10,costMonth11,costMonth12:S-getCostsByInvoiceItemType(invoiceItemTypeId,departmentId)<isParent,isMulti,costMonth1,costMonth2,costMonth3,costMonth4,costMonth5,costMonth6,costMonth7,costMonth8,costMonth9,costMonth10,costMonth11,costMonth12>" 
			/>
			<div class='row-fluid'>
				<div class="span12">
					<button id="buttonCancel" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
					<button id="buttonSave" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
				</div>
			</div>
		<#else>
			<@jqGrid id="jqxgridCosts" filterable="true" viewSize="20" filtersimplemode="true" addType="popup" dataField=dataField columnlist=columnList clearfilteringbutton="true" columngrouplist=""
				showtoolbar="true" addrow="true" deleterow="false" alternativeAddPopup="alterpopupWindow" editable="true" selectionmode="checkbox" editmode="click"
				url="jqxGeneralServicer?sname=getLogCostsDetail&departmentId=${parameters.departmentId?if_exists}" otherParams="isParent,isMulti,costMonth1,costMonth2,costMonth3,costMonth4,costMonth5,costMonth6,costMonth7,costMonth8,costMonth9,costMonth10,costMonth11,costMonth12:S-getCostsByInvoiceItemType(invoiceItemTypeId,departmentId)<isParent,isMulti,costMonth1,costMonth2,costMonth3,costMonth4,costMonth5,costMonth6,costMonth7,costMonth8,costMonth9,costMonth10,costMonth11,costMonth12>" 
			/>
			<div class='row-fluid'>
				<div class="span12">
					<button id="buttonCancel" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
					<button id="buttonSave" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
				</div>
			</div>
		</#if>
	<#else>
		<@jqGrid id="jqxgridCosts" defaultSortColumn="departmentId" filterable="true" viewSize="20" filtersimplemode="true" addType="popup" dataField=dataField columnlist=columnList clearfilteringbutton="true" columngrouplist=""
			showtoolbar="true" addrow="false" deleterow="false" alternativeAddPopup="" editable="false" selectionmode="" editmode="click"
			url="jqxGeneralServicer?sname=getLogCostsDetail&departmentId=${parameters.departmentId?if_exists}" otherParams="isParent,isMulti,costMonth1,costMonth2,costMonth3,costMonth4,costMonth5,costMonth6,costMonth7,costMonth8,costMonth9,costMonth10,costMonth11,costMonth12:S-getCostsByInvoiceItemType(invoiceItemTypeId,departmentId)<isParent,isMulti,costMonth1,costMonth2,costMonth3,costMonth4,costMonth5,costMonth6,costMonth7,costMonth8,costMonth9,costMonth10,costMonth11,costMonth12>"
		/>
	</#if>
</div>
<div id="alterpopupWindow" class="hide">
	<div class="row-fluid">
		${uiLabelMap.CreateNewCosts}
	</div>
	<div class='form-window-container'>
    	<div class='form-window-content'>
        	<div class="row-fluid">
        		<div class="span6">
        			<div class="row-fluid">	
        				<div class="span5" style="text-align: right">
        					<div style="height: 30px; margin-top: 10px"> ${uiLabelMap.CostsType}: </div>
        					<div style="height: 30px; margin-top: 6px"> ${uiLabelMap.DetailCostType}: </div>
        					<div style="height: 30px; margin-top: 6px" id="vehicleLabel"> ${uiLabelMap.Vehicle}: </div>
        					<div style="height: 30px; margin-top: 6px" id="vendorLabel"> ${uiLabelMap.Vendor}: </div>
        				</div>
        				<div class="span7">	
        					<div id="parentTypeId" style="width: 100%;" class="green-label margin-top10"></div>
        					<div id="invoiceItemTypeId" style="width: 100%;" class="green-label margin-top10"></div>
        					<div id="vehicleId" style="width: 100%;" class="green-label margin-top10"></div>
        					<div id="vendorId" style="width: 100%;" class="green-label margin-top10"></div>
        				</div>
        			</div>
        		</div>
        		<div class="span6 no-left-margin">
        			<div class="row-fluid">	
        				<div class="span5" style="text-align: right">
        					<div style="height: 30px; margin-top: 10px">${uiLabelMap.CostTemporary}:</div>
        					<div style="height: 30px; margin-top: 10px" class="asterisk">${uiLabelMap.CostValue}:</div>
        					<div style="height: 30px; margin-top: 6px">${uiLabelMap.currencyUomId}:</div>
        					<div style="height: 30px; margin-top: 6px">${uiLabelMap.CreateDate}:</div>
        				</div>
        				<div class="span7">
        					<div style="display: inline-block"><input class="margin-top10" style='margin-bottom: 0px !important; height: 17.5px; width: 188px !important' type='number' id="costsTemporary"></input></div>
        					<div style="display: inline-block"><input class="margin-top10" style='margin-bottom: 0px !important; height: 17.5px; width: 188px !important' type='number' id="costsValue"></input></div>
        					<div id="currencyUomId" style="height: 30px; width: 100%;" class="green-label margin-top10"></div>
        					<div id="invoiceDate" style="height: 30px; width: 100%;" class="green-label margin-top10"></div>
        				</div>
        			</div>
        		</div>
    	    </div>
    	    <div class="row-fluid">
    	    	<div class="span6">
	    	    	<div class="span5" style="text-align: right">
	    	    		<div style="height: 30px; margin-top: 6px"> ${uiLabelMap.Description}: </div>
	    	    	</div>
	    	    	<div class="span7 margin-top10">
		        		<textarea id="editor"></textarea>
	    	    	</div>
    	    	</div>
    	    </div>
    	</div>
    	<div class="form-action">
            <div class='row-fluid'>
                <div class="span12 margin-top20" style="margin-bottom:10px;">
                    <button id="addButtonCancel" class='btn btn-danger form-action-button pull-right' style="margin-right:20px !important;"><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
                    <button id="addButtonSave" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
                </div>
            </div>
        </div>
	</div>
</div>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxeditor.js"></script>
<script type="text/javascript" src="/aceadmin/assets/js/bootbox.min.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script type="text/javascript">
	$('#document').ready(function(){
		$("#alterpopupWindow").jqxWindow({
			maxWidth: 1500, minWidth: 800, minHeight: 430, modalZIndex: 1000, maxHeight: 1200, resizable: true,  isModal: true, autoOpen: false, modalOpacity: 0.7, theme:theme           
		});
		$('#editor').jqxEditor({
            height: '100%',
            width: '582px',
            theme: theme,
        });
		$('#jqxgridCosts').on('rowDoubleClick', function (event){
			var selectedrowindex = $("#jqxgridCosts").jqxGrid('getselectedrowindex');
	        if (selectedrowindex >= 0) {
	            var id = $("#jqxgridCosts").jqxGrid('getrowid', selectedrowindex);
	            var data = $("#jqxgridCosts").jqxGrid('getrowdata', id);
	        }
		});
		var childTypeData = new Array();
		var vehicleData = new Array();
		var vendorData = new Array();
		
		$("#parentTypeId").jqxDropDownList({source: parentTypeData, autoDropDownHeight:true, displayMember:"description", selectedIndex: 0, valueMember: "invoiceItemTypeId"});
		$("#invoiceItemTypeId").jqxDropDownList({source: childTypeData, autoDropDownHeight:true, displayMember:"description", selectedIndex: 0, valueMember: "invoiceItemTypeId"});
		$("#currencyUomId").jqxDropDownList({source: currencyUomData, autoDropDownHeight:true, displayMember:"description", valueMember: "currencyUomId"});
		$("#currencyUomId").jqxDropDownList('val', 'VND');
		$("#vehicleId").jqxDropDownList({source: vehicleData, autoDropDownHeight:true, displayMember:"vehicleName", selectedIndex: 0, valueMember: "vehicleId"});
		$("#vendorId").jqxDropDownList({source: vendorData, autoDropDownHeight:true, displayMember:"vendorId", selectedIndex: 0, valueMember: "vendorId"});
		$("#invoiceDate").jqxDateTimeInput({ width: '200px', height: '25px', formatString: 'dd/MM/yyyy'});
		$("#invoiceDate").jqxDateTimeInput({disabled: true});
		update({
			parentTypeId: $("#parentTypeId").val(),
			departmentId: '${parameters.departmentId?if_exists}',
		}, 'getInvoiceItemTypeByParent' , 'listInvoiceItemTypes', 'invoiceItemTypeId', 'description', 'invoiceItemTypeId');
		
		if (!$("#invoiceItemTypeId").val()){
			$("#invoiceItemTypeId").jqxDropDownList({ disabled: true }); 
			if ("AUTO_DRIVE_COST" == $("#parentTypeId").val()){
				update({
					invoiceItemTypeId: $("#parentTypeId").val(),
					objectId: 'vehicleId',
				}, 'getObjectByInvoiceItemType' , 'listObjects', 'vehicleId', 'vehicleName', 'vehicleId');
			} else if ("VEHICLE_DEPRE_COST" == $("#parentTypeId").val()){
				update({
					invoiceItemTypeId: $("#parentTypeId").val(),
					objectId: 'vehicleId',
				}, 'getObjectByInvoiceItemType' , 'listObjects', 'vehicleId', 'vehicleName', 'vehicleId');
			} else if ("DLV_3PL_COST" == $("#parentTypeId").val()){
				update({
					invoiceItemTypeId: $("#parentTypeId").val(),
					objectId: 'vendorId',
				}, 'getObjectByInvoiceItemType' , 'listObjects', 'partyIdTo', 'partyIdTo', 'vendorId');
			}
		} else {
			$("#invoiceItemTypeId").jqxDropDownList({ disabled: false }); 
			if ("AUTO_DRIVE_COST" == $("#parentTypeId").val()){
				update({
					invoiceItemTypeId: $("#invoiceItemTypeId").val(),
					objectId: 'vehicleId',
				}, 'getObjectByInvoiceItemType' , 'listObjects', 'vehicleId', 'vehicleName', 'vehicleId');
			} else if ("VEHICLE_DEPRE_COST" == $("#parentTypeId").val()){
				update({
					invoiceItemTypeId: $("#invoiceItemTypeId").val(),
					objectId: 'vehicleId',
				}, 'getObjectByInvoiceItemType' , 'listObjects', 'vehicleId', 'vehicleName', 'vehicleId');
			} else if ("DLV_3PL_COST" == $("#parentTypeId").val()){
				update({
					invoiceItemTypeId: $("#invoiceItemTypeId").val(),
					objectId: 'vendorId',
				}, 'getObjectByInvoiceItemType' , 'listObjects', 'partyIdTo', 'partyIdTo', 'vendorId');
			} 
		}
		if ($("#vehicleId").val()){
			$("#vehicleId").jqxDropDownList({ disabled: false }); 
		} else{
			$("#vehicleLabel").attr("disabled", "disabled").off('click');
		}
		if ($("#vendorId").val()){
			$("#vendorId").jqxDropDownList({ disabled: false });
		} else {
			$("#vendorId").jqxDropDownList({ disabled: true });
		}
		
		$('#costsValue').jqxValidator({
			rules:[{
				input: '#costsValue', 
	            message: '${StringUtil.wrapString(uiLabelMap.FieldRequired?default(''))}', 
	            action: 'blur', 
	            rule: function (input) {	
	            	var tmp = $("#costsValue").val();
	                return tmp ? true : false;
	            }
			}],
		});
	});
	
	$("#parentTypeId").on('change', function(event){
		update({
			parentTypeId: $("#parentTypeId").val(),
			departmentId: '${parameters.departmentId?if_exists}',
		}, 'getInvoiceItemTypeByParent' , 'listInvoiceItemTypes', 'invoiceItemTypeId', 'description', 'invoiceItemTypeId');
		if ("AUTO_DRIVE_COST" == $("#parentTypeId").val()){
			$("#vendorId").jqxDropDownList({ disabled: true });
			$("#vehicleId").jqxDropDownList({ disabled: false });
			update({
				invoiceItemTypeId: $("#parentTypeId").val(),
				objectId: 'vehicleId',
			}, 'getObjectByInvoiceItemType' , 'listObjects', 'vehicleId', 'vehicleName', 'vehicleId');
		} else if ("VEHICLE_DEPRE_COST" == $("#parentTypeId").val()){
			$("#vendorId").jqxDropDownList({ disabled: true });
			$("#vehicleId").jqxDropDownList({ disabled: false });
			update({
				invoiceItemTypeId: $("#parentTypeId").val(),
				objectId: 'vehicleId',
			}, 'getObjectByInvoiceItemType' , 'listObjects', 'vehicleId', 'vehicleName', 'vehicleId');
		} else if ("DLV_3PL_COST" == $("#parentTypeId").val()){
			$("#vendorId").jqxDropDownList({ disabled: false });
			$("#vehicleId").jqxDropDownList({ disabled: true });
			update({
				invoiceItemTypeId: $("#parentTypeId").val(),
				objectId: 'vendorId',
			}, 'getObjectByInvoiceItemType' , 'listObjects', 'partyIdTo', 'partyIdTo', 'vendorId');
		} else if ("DLV_TOOLS_KIT_COST" == $("#parentTypeId").val()){
			$("#vendorId").jqxDropDownList({ disabled: true });
			$("#vehicleId").jqxDropDownList({ disabled: true });
		} else {
			$("#vendorId").jqxDropDownList({ disabled: false });
			$("#vehicleId").jqxDropDownList({ disabled: false });
		}
		if (!$("#invoiceItemTypeId").val()){
			$("#invoiceItemTypeId").jqxDropDownList({ disabled: true }); 
		} else {
			$("#invoiceItemTypeId").jqxDropDownList({ disabled: false }); 
		}
	});
	
	
	$("#addButtonSave").click(function () {
		$('#costsValue').jqxValidator('validate');
		bootbox.confirm("${uiLabelMap.DAAreYouSureSave}",function(result){ 
			if(result){
				var tmp = $('#invoiceDate').jqxDateTimeInput('value');
				jQuery.ajax({
			        url: "addLogisticsCosts",
			        type: "POST",
			        async: false,
			        data: {
			        	invoiceDate: tmp.getTime(),
			        	invoiceItemTypeId: $('#invoiceItemTypeId').val(),
			        	parentTypeId: $('#parentTypeId').val(),
			        	costsTemporary: $('#costsTemporary').val(),
			        	costsValue: $('#costsValue').val(),
			        	vehicleId: $('#vehicleId').val(),
			        	vendorId: $('#vendorId').val(),
			        	departmentId: departmentId,
			        },
			        success: function(res) {
			        	$("#jqxgridCosts").jqxGrid('updatebounddata');
			        	$("#alterpopupWindow").jqxWindow('close');
			        }
			    });
			}
		});
	});
	
	$("#buttonSave").click(function () {
		var row;
		var selectedIndexs = $('#jqxgridCosts').jqxGrid('getselectedrowindexes');
		if(selectedIndexs.length == 0){
			bootbox.dialog("${uiLabelMap.DAYouNotYetChooseRow}!", [{
                "label" : "OK",
                "class" : "btn btn-primary standard-bootbox-bt",
                "icon" : "fa fa-check",
                }]
            );
            return false;
		} else {
			bootbox.confirm("${uiLabelMap.DAAreYouSureSave}",function(result){ 
				if(result){
					var listCosts = new Array();
					for(var i = 0; i < selectedIndexs.length; i++){
						var data = $('#jqxgridCosts').jqxGrid('getrowdata', selectedIndexs[i]);
						var map = {};
						map['month'] = curMonth;
						map['year'] = curYear;
						map['departmentId'] = data.departmentId;
						map['invoiceItemTypeId'] = data.invoiceItemTypeId;
						switch (curMonth)
						{
						   case 1:
							   map['costPriceActual'] = data.costMonth1;
							   break;
						   case 2:
							   map['costPriceActual'] = data.costMonth2;
							   break;
						   case 3:
							   map['costPriceActual'] = data.costMonth3;
							   break;
						   case 4:
							   map['costPriceActual'] = data.costMonth4;
							   break;
						   case 5:
							   map['costPriceActual'] = data.costMonth5;
							   break;
						   case 6:
							   map['costPriceActual'] = data.costMonth6;
							   break;
						   case 7:
							   map['costPriceActual'] = data.costMonth7;
							   break;
						   case 8:
							   map['costPriceActual'] = data.costMonth8;
							   break;
						   case 9:
							   map['costPriceActual'] = data.costMonth9;
							   break;
						   case 10:
							   map['costPriceActual'] = data.costMonth10;
							   break;
						   case 11:
							   map['costPriceActual'] = data.costMonth11;
							   break;
						   case 12:
							   map['costPriceActual'] = data.costMonth12;
							   break;
						}
						listCosts[i] = map;
					}
					listCosts = JSON.stringify(listCosts);
					jQuery.ajax({
				        url: "updateLogisticsCosts",
				        type: "POST",
				        async: false,
				        data: {
				        	month: curMonth,
				        	year: curYear,
				        	listCosts: listCosts,
				        },
				        success: function(res) {
				        	$("#jqxgridCosts").jqxGrid('updatebounddata');
				        }
				    });
				}
			});
		}
	});
	$("#buttonCancel").click(function () {
		$("#jqxgridCosts").jqxGrid('updatebounddata');
	});
	
	function renderHtml(data, key, value, id){
		var y = "";
		var source = new Array();
		var index = 0;
		for (var x in data){
			index = source.length;
			var row = {};
			row[key] = data[x][key];
			row[value] = data[x][value];
			source[index] = row;
		}
		if($("#"+id).length){
			$("#"+id).jqxDropDownList('clear');
			$("#"+id).jqxDropDownList({source: source, selectedIndex: 0});
		}
	}
	
    function update(jsonObject, url, data, key, value, id) {
	    jQuery.ajax({
	        url: url,
	        type: "POST",
	        data: jsonObject,
	        async: false,
	        success: function(res) {
	        	var json = res[data];
	            renderHtml(json, key, value, id);
	        }
	    });
	}
</script>