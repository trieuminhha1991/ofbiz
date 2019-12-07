<script type="text/javascript">
<#assign localeStr = "VI" />
	<#if locale = "en">
<#assign localeStr = "EN" />
	</#if>
	var departmentId = '${parameters.departmentId}';
	var invoiceItemTypeId = 'RENTED_WH_COST';
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
<div>
<#assign dataFieldFacilityRent = "[
	{name: 'facilityId', type: 'String'},
	{name: 'facilityName', type: 'String'},
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
]">

<#assign columnListFacilityRentCost ="
{ text: '${uiLabelMap.FacilityId}', datafield: 'facilityName', editable: false, hidden: false, width: '15%', pinned: true,
		cellsrenderer: function (row, column, value){
			var data = $('#jqxgridFacilityRent').jqxGrid('getrowdata', row);
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
			var data = $('#jqxgridFacilityRent').jqxGrid('getrowdata', row);
			var cost = parseInt(value);
			return '<span>'+cost.toLocaleString('${localeStr}')+'<span>';
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
			var data = $('#jqxgridFacilityRent').jqxGrid('getrowdata', row);
			var cost = parseInt(value);
			return '<span>'+cost.toLocaleString('${localeStr}')+'<span>';
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
			var data = $('#jqxgridFacilityRent').jqxGrid('getrowdata', row);
			var cost = parseInt(value);
			return '<span>'+cost.toLocaleString('${localeStr}')+'<span>';
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
			var data = $('#jqxgridFacilityRent').jqxGrid('getrowdata', row);
			var cost = parseInt(value);
			return '<span>'+cost.toLocaleString('${localeStr}')+'<span>';
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
			var data = $('#jqxgridFacilityRent').jqxGrid('getrowdata', row);
			var cost = parseInt(value);
			return '<span>'+cost.toLocaleString('${localeStr}')+'<span>';
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
			var data = $('#jqxgridFacilityRent').jqxGrid('getrowdata', row);
			var cost = parseInt(value);
			return '<span>'+cost.toLocaleString('${localeStr}')+'<span>';
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
			var data = $('#jqxgridFacilityRent').jqxGrid('getrowdata', row);
			var cost = parseInt(value);
			return '<span>'+cost.toLocaleString('${localeStr}')+'<span>';
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
			var data = $('#jqxgridFacilityRent').jqxGrid('getrowdata', row);
			var cost = parseInt(value);
			return '<span>'+cost.toLocaleString('${localeStr}')+'<span>';
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
			var data = $('#jqxgridFacilityRent').jqxGrid('getrowdata', row);
			var cost = parseInt(value);
			return '<span>'+cost.toLocaleString('${localeStr}')+'<span>';
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
			var data = $('#jqxgridFacilityRent').jqxGrid('getrowdata', row);
			var cost = parseInt(value);
			return '<span>'+cost.toLocaleString('${localeStr}')+'<span>';
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
			var data = $('#jqxgridFacilityRent').jqxGrid('getrowdata', row);
			var cost = parseInt(value);
			return '<span>'+cost.toLocaleString('${localeStr}')+'<span>';
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
			var data = $('#jqxgridFacilityRent').jqxGrid('getrowdata', row);
			var cost = parseInt(value);
			return '<span>'+cost.toLocaleString('${localeStr}')+'<span>';
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
"/>

<@jqGrid id="jqxgridFacilityRent" filterable="true" viewSize="10" filtersimplemode="true" addType="popup" dataField=dataFieldFacilityRent columnlist=columnListFacilityRentCost clearfilteringbutton="true" columngrouplist=""
	showtoolbar="true" addrow="false" deleterow="false" alternativeAddPopup="" editable="true" editmode="click"
	otherParams="costMonth1,costMonth2,costMonth3,costMonth4,costMonth5,costMonth6,costMonth7,costMonth8,costMonth9,costMonth10,costMonth11,costMonth12:S-getCostsByFacility(facilityId)<costMonth1,costMonth2,costMonth3,costMonth4,costMonth5,costMonth6,costMonth7,costMonth8,costMonth9,costMonth10,costMonth11,costMonth12>"
	url="jqxGeneralServicer?sname=getListRentedFacilities" 
/>
</div>
<div class='row-fluid'>
    <div class="span12">
        <button id="addButtonCancel" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
        <button id="addButtonSave" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
    </div>
</div>
