<script>
	<#assign localeStr = "VI" />
	var localeStr = "VI";
	<#if locale = "en">
		<#assign localeStr = "EN" />
		localeStr = "EN";
	</#if>
	
	<#assign company = Static["com.olbius.basehr.util.MultiOrganizationUtil"].getCurrentOrganization(delegator, userLogin.get("userLoginId"))!/>
	<#assign uomList = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", "PRODUCT_PACKING"), null, null, null, false) />
	var uomData = 
	[
		<#list uomList as uom>
		{
			uomId: "${uom.uomId}",
			description: "${StringUtil.wrapString(uom.get('description', locale)?if_exists)}"
		},
		</#list>
	];
	function getDescriptionByUomId(uomId) {
		for ( var x in uomData) {
			if (uomId == uomData[x].uomId) {
				return uomData[x].description;
			}
		}
	}

	function reponsiveRowDetails(grid, parentElement) {
	    $(window).bind('resize', function() {
	    	$(grid).jqxGrid({ width: "96%"});
	    });
	    $('#sidebar').bind('resize', function() {
	    	$(grid).jqxGrid({ width: "96%"});
	    });
	}
	
if (uiLabelMap == undefined) var uiLabelMap = {};
uiLabelMap.FieldRequired = "${StringUtil.wrapString(uiLabelMap.FieldRequired)}";
uiLabelMap.PleaseSelectTitle = "${StringUtil.wrapString(uiLabelMap.PleaseSelectTitle)}";
uiLabelMap.CannotBeforeNow = "${StringUtil.wrapString(uiLabelMap.CannotBeforeNow)}";
uiLabelMap.CannotAfterNow = "${StringUtil.wrapString(uiLabelMap.CannotAfterNow)}";
uiLabelMap.AreYouSureCreate = "${StringUtil.wrapString(uiLabelMap.AreYouSureCreate)}";
uiLabelMap.CommonCancel = "${StringUtil.wrapString(uiLabelMap.CommonCancel)}";
uiLabelMap.AreYouSureDetele = "${StringUtil.wrapString(uiLabelMap.AreYouSureDetele)}";
uiLabelMap.CommonSave = "${StringUtil.wrapString(uiLabelMap.CommonSave)}";
uiLabelMap.OK = "${StringUtil.wrapString(uiLabelMap.OK)}";
uiLabelMap.ClickToChoose = "${StringUtil.wrapString(uiLabelMap.ClickToChoose)}";
uiLabelMap.YouNotYetChooseProduct = "${StringUtil.wrapString(uiLabelMap.YouNotYetChooseProduct)}";
uiLabelMap.YouNotYetChooseLabel = "${StringUtil.wrapString(uiLabelMap.YouNotYetChooseLabel)}";
uiLabelMap.AreYouSureDelete = "${StringUtil.wrapString(uiLabelMap.AreYouSureDelete)}";
uiLabelMap.CommonDelete = "${StringUtil.wrapString(uiLabelMap.CommonDelete)}";
uiLabelMap.QuickAssign = "${StringUtil.wrapString(uiLabelMap.QuickAssign)}";
uiLabelMap.AreYouSureSave = "${StringUtil.wrapString(uiLabelMap.AreYouSureSave)}";

</script>
<div>
	<div class="row-fluid margin-top10">
		<div class="span12">
			<div id="jqxgridInventorySelected" style="width: 100%"></div>
		</div>
	</div>
</div>

<#assign dataFieldConfirm="[
{ name: 'quantity', type: 'number' },
{ name: 'inventoryItemLabelId', type: 'string' },
{ name: 'inventoryItemLabelTypeId', type: 'string' },
{ name: 'description', type: 'string' },
{ name: 'quantityUomId', type: 'string' },
{ name: 'packingUomIds', type: 'string' },
{ name: 'productId', type: 'string'},
{ name: 'productName', type: 'string'},
{ name: 'productCode', type: 'string'},
{ name: 'ownerPartyId', type: 'string'},
{ name: 'facilityId', type: 'string'},
{ name: 'datetimeManufactured', type: 'date', other: 'Timestamp'},
{ name: 'expireDate', type: 'date', other: 'Timestamp'},
{ name: 'quantityOnHandTotal', type: 'number' },
{ name: 'statusId', type: 'string' },
{ name: 'lotId', type: 'string' }]"/>
<#assign columnlistConfirm="
{ text: '${uiLabelMap.SequenceId}', sortable: false, filterable: false, editable: false, pinned: true,
    groupable: false, draggable: false, resizable: false,
    datafield: '', columntype: 'number', width: 50,
    cellsrenderer: function (row, column, value) {
        return '<div style=margin:4px;>' + (value + 1) + '</div>';
    }
},
{ text: '${uiLabelMap.ProductId}', datafield: 'productCode', align: 'left', width: 130, pinned: true, editable: false,},
{ text: '${uiLabelMap.ProductName}', datafield: 'productName', align: 'left', minwidth: 250,editable: false,},
	{ text: '${uiLabelMap.Description}', dataField: 'description', minwidth: 300, filtertype:'input', editable: false,},
	{text: '${StringUtil.wrapString(uiLabelMap.Quantity)}', dataField: 'quantity', width: 120, cellsalign: 'right', filterable:false, sortable: false, columntype: 'numberinput', cellsformat: 'd',
		cellsrenderer: function(row, column, value){
			if (value != undefined && value != null && value != ''){
				return '<span style=\"text-align: right;\" title=' + value.toLocaleString('${localeStr}') + '>' + value.toLocaleString('${localeStr}') + '</span>';
			} 
	 	},
	},
	{ text: '${uiLabelMap.Unit}', datafield: 'quantityUomId', align: 'left', width: 100,editable: false,
		cellsrenderer: function(row, colum, value){
			if(value){
				var data = $('#jqxgridInventorySelected').jqxGrid('getrowdata', row);
				var des = null;
				for (var i = 0; i < uomData.length; i ++){
					if (data.quantityUomId == uomData[i].uomId){
						des = uomData[i].description;
					}
				}
				return '<span>' + des + '</span>';
			}
	    }, 
	},
	{ text: '${uiLabelMap.QOH}', datafield: 'quantityOnHandTotal', align: 'left',editable: false, width: 90, cellsalign: 'right', filtertype: 'number',
		cellsrenderer: function(row, colum, value){
			if(value){
				return '<span style=\"text-align: right;\">' + value.toLocaleString('${localeStr}') + '</span>';
			}
	    }, 
	    rendered: function(element){
	    	$(element).jqxTooltip({content: '${StringUtil.wrapString(uiLabelMap.QuantityOnHandTotal)}', theme: 'orange' });
	    }, 
	},
	{ text: '${StringUtil.wrapString(uiLabelMap.ProductManufactureDate)}', dataField: 'datetimeManufactured',editable: false, align: 'left', width: 110, columntype: 'datetimeinput', filtertype: 'range', cellsformat: 'dd/MM/yyyy', cellsalign: 'right',
		cellsrenderer: function(row, colum, value){
			if(value === null || value === undefined || value === ''){
				return '<span style=\"text-align: right;\">_NA_</span>';
			}
		}
	},
	{ text: '${StringUtil.wrapString(uiLabelMap.ProductExpireDate)}', dataField: 'expireDate',editable: false, align: 'left', width: 110, columntype: 'datetimeinput', filtertype: 'range', cellsformat: 'dd/MM/yyyy', cellsalign: 'right',
		cellsrenderer: function(row, colum, value){
			if(value === null || value === undefined || value === ''){
				return '<span style=\"text-align: right;\">_NA_</span>';
			}
		}
	},

	{ text: '${uiLabelMap.Batch}', datafield: 'lotId', align: 'left', width: 80,editable: false,
		cellsrenderer: function(row, colum, value){
			if(value === null || value === undefined || value === ''){
				return '<span style=\"text-align: right;\">_NA_</span>';
			} else {
				return '<span style=\"text-align: right;\">'+value+'</span>';
			}
		}	
	},
">
<@jqGrid filtersimplemode="true" id="jqxgridInventorySelected" filterable="false" dataField=dataFieldConfirm columnlist=columnlistConfirm editable="false" showtoolbar="false"
	url="" editmode='click' selectionmode='multiplecellsadvanced'
/>
<script type="text/javascript" src="/logresources/js/util/UtilValidate.js"></script>