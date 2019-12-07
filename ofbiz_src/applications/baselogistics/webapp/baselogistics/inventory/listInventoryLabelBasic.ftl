<script>
	<#assign localeStr = "VI" />
	var localeStr = "VI";
	<#if locale = "en">
		<#assign localeStr = "EN" />
		localeStr = "EN";
	</#if>
	var allowSelected = false;
	<#assign company = Static["com.olbius.basehr.util.MultiOrganizationUtil"].getCurrentOrganization(delegator, userLogin.get("userLoginId"))!/>
	<#assign listTypes = ["PRODUCT_PACKING", "WEIGHT_MEASURE"]>
	<#assign uomList2 = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", Static["org.ofbiz.entity.condition.EntityOperator"].IN, listTypes), null, null, null, false) />
	var uomData2 = 
	[
		<#list uomList2 as uom>
			<#if uom.uomTypeId == "WEIGHT_MEASURE">
				{
					uomId: "${uom.uomId}",
					description: "${StringUtil.wrapString(uom.get('abbreviation', locale)?if_exists)}"
				},
			<#else>
				{
				uomId: "${uom.uomId}",
				description: "${StringUtil.wrapString(uom.get('description', locale)?if_exists)}"
				},
			</#if>
		</#list>
	];
	
	<#assign labelTypeList = delegator.findList("InventoryItemLabelType", null, null, null, null, false) />
	var invLabelTypeData = 
	[
		<#list labelTypeList as item>
		{
			inventoryItemLabelTypeId: "${item.inventoryItemLabelTypeId}",
			description: "${StringUtil.wrapString(item.get('description', locale)?if_exists)}"
		},
		</#list>
	];
	
	function getUomDescription(uomId) {
		for ( var x in uomData2) {
			if (uomId == uomData2[x].uomId) {
				return uomData2[x].description;
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
<div id="detailItems">
<#assign initrowdetails = "function (index, parentElement, gridElement, datarecord) {
	var grid = $($(parentElement).children()[0]);
	$(grid).attr('id','jqxgridItemAndLabel'+index);
	reponsiveRowDetails(grid);
	var sourceGridDetail =
    {
        localdata: datarecord.rowDetail,
        datatype: 'local',
        datafields: [
			{ name: 'quantity', type: 'number' },
			{ name: 'inventoryItemLabelId', type: 'string' },
			{ name: 'inventoryItemLabelTypeId', type: 'string' },
			{ name: 'description', type: 'string' },
			{ name: 'quantityUomId', type: 'string' },
			{ name: 'packingUomIds', type: 'string' },
			{ name: 'productId', type: 'string'},
			{ name: 'ownerPartyId', type: 'string'},
			{ name: 'productCode', type: 'string' },
			{ name: 'productName', type: 'string' },
			{ name: 'facilityId', type: 'string'},
            { name: 'datetimeManufactured', type: 'date', other: 'Timestamp'},
            { name: 'expireDate', type: 'date', other: 'Timestamp'},
			{ name: 'quantityOnHandTotal', type: 'number' },
			{ name: 'amountOnHandTotal', type: 'number' },
			{ name: 'statusId', type: 'string' },
			{ name: 'requireAmount', type: 'string' },
			{ name: 'lotId', type: 'string' }
			]
    };
    var dataAdapterGridDetail = new $.jqx.dataAdapter(sourceGridDetail);
    grid.jqxGrid({
        width: '98%',
        height: 190,
        theme: 'olbius',
        localization: getLocalization(),
        source: dataAdapterGridDetail,
        sortable: true,
        pagesize: 5,
 		pageable: true,
 		editable: false,
 		columnsresize: true,
        selectionmode: 'checkbox',
        columns: [{ text: '${uiLabelMap.SequenceId}', sortable: false, filterable: false, editable: false, pinned: true,
					    groupable: false, draggable: false, resizable: false,
					    datafield: '', columntype: 'number', width: 50,
					    cellsrenderer: function (row, column, value) {
					        return '<div style=margin:4px;>' + (value + 1) + '</div>';
					    }
					},
					{ text: '${uiLabelMap.BLInventoryItemLabelId}', dataField: 'inventoryItemLabelId', width: 200, filtertype:'input', editable: false,},
					{ text: '${uiLabelMap.BLInventoryItemLabeTypelId}', dataField: 'inventoryItemLabelTypeId', width: 200, filtertype:'input', editable: false,
						cellsrenderer: function(row, column, value){
							var data = grid.jqxGrid('getrowdata', row);
				   			for (var i = 0 ; i < invLabelTypeData.length; i++){
								if (value == invLabelTypeData[i].inventoryItemLabelTypeId){
									return '<span title=' + invLabelTypeData[i].description + '>' + invLabelTypeData[i].description + '</span>';
								}
							}
			   				return value;
						},
					},
					{ text: '${uiLabelMap.Description}', dataField: 'description', minwidth: 200, filtertype:'input', editable: false,},
				]
        });
    	
    	$(grid).on('rowselect', function (event) 
		{
    		var listLabelSelecteds = $(grid).jqxGrid('getselectedrowindexes');
    		if (listLabelSelecteds.length > 0){
    			var index = datarecord.uid;
        		allowSelected = true;
        		$('#jqxgridItemAndLabel').jqxGrid('selectrow', index);
        		allowSelected = false;
    		} else {
    			jOlbUtil.alert.error(uiLabelMap.YouNotYetChooseLabel);
				return false;
    		}
		});
    	$(grid).on('rowunselect', function (event) 
		{
    		var index = datarecord.uid;
    		if ($('#jqxgridItemAndLabel'+ index).length > 0){
				var childIndexs = $('#jqxgridItemAndLabel'+ index).jqxGrid('getselectedrowindexes');
    			if (childIndexs.length <=0 ){
    				$('#jqxgridItemAndLabel').jqxGrid('unselectrow', index);
    				var id = $('#jqxgridItemAndLabel').jqxGrid('getrowid', index);
    				$('#jqxgridItemAndLabel').jqxGrid('setcellvaluebyid', id, 'quantity', 0);
    			}
			}
    		allowSelected == false
		});
 }"/>
	
	<#assign dataFieldDetail="[
					{ name: 'productId', type: 'string'},
					{ name: 'productCode', type: 'string' },
					{ name: 'productName', type: 'string' },
					{ name: 'facilityId', type: 'string'},
					{ name: 'facilityName', type: 'string'},
	                { name: 'datetimeManufactured', type: 'date', other: 'Timestamp'},
	                { name: 'expireDate', type: 'date', other: 'Timestamp'},
	                { name: 'quantity', type: 'number' },
					{ name: 'quantityOnHandTotal', type: 'number' },
					{ name: 'amountOnHandTotal', type: 'number' },
					{ name: 'availableToPromiseTotal', type: 'number' },
					{ name: 'quantityUomId', type: 'string' },
					{ name: 'weightUomId', type: 'string' },
					{ name: 'statusId', type: 'string' },
					{ name: 'ownerPartyId', type: 'string'},
					{ name: 'statusDesc', type: 'string' },
					{ name: 'inventoryItemLabelId', type: 'string' },
					{ name: 'description', type: 'string'},
					{ name: 'rowDetail', type: 'string'},
					{ name: 'requireAmount', type: 'string' },
					{ name: 'lotId', type: 'string' }]"/>
	<#assign columnlistDetail="
					{ text: '${uiLabelMap.SequenceId}', sortable: false, filterable: false, editable: false, pinned: true,
					    groupable: false, draggable: false, resizable: false,
					    datafield: '', columntype: 'number', width: 50,
					    cellsrenderer: function (row, column, value) {
					        return '<div style=margin:4px;>' + (value + 1) + '</div>';
					    }
					},
					{ text: '${uiLabelMap.Facility}', datafield: 'facilityName',editable: false, align: 'left', width: 150, pinned: true},
					{ text: '${uiLabelMap.ProductId}', datafield: 'productCode',editable: false, align: 'left', width: 150, pinned: true},
					{ text: '${uiLabelMap.ProductName}', datafield: 'productName',editable: false, align: 'left', width: 250},
					{ text: '${uiLabelMap.BLProductLabelAssigned}', datafield: 'description',editable: false, align: 'left', minwidth: 200, filterable: true,
						cellsrenderer: function(row, colum, value){
							if(value === null || value === undefined || value === ''){
								return '<span></span>';
							}
					    }, 
					},
					{ text: '${uiLabelMap.QOH}', datafield: 'quantityOnHandTotal', editable: false, align: 'left', width: 120, cellsalign: 'right', filtertype: 'number',
						cellsrenderer: function(row, colum, value){
							if(value){
								var data = grid.jqxGrid('getrowdata', row);
								var requireAmount = data.requireAmount;
					   			if (requireAmount && requireAmount == 'Y') {
					   				value = data.amountOnHandTotal;
					   			}
								return '<span style=\"text-align: right;\">' + formatnumber(value) + '</span>';
							} else {
								return '<span style=\"text-align: right;\">0</span>';
							}
							return value;
					    }, 
					    rendered: function(element){
					    	$(element).jqxTooltip({content: '${StringUtil.wrapString(uiLabelMap.QuantityOnHandTotal)}', theme: 'orange' });
					    }, 
					},
					{ text: '${uiLabelMap.Quantity}', dataField: 'quantity', columntype: 'numberinput', width: 120, editable: true,
						cellsrenderer: function(row, column, value){
							var data = grid.jqxGrid('getrowdata', row);
							if (value){
								return '<span style=\"text-align: right\">' + value + '</span>';
							} else {
								return '<span style=\"text-align: right\">' + 0	 + '</span>';
							}
						},
						initeditor: function (row, cellvalue, editor, celltext, pressedChar) {
							var data = grid.jqxGrid('getrowdata', row);
							var requireAmount = data.requireAmount;
							var qoh = data.quantityOnHandTotal;
				   			if (requireAmount && requireAmount == 'Y') {
				   				qoh = data.amountOnHandTotal;
				   			}
							if (!data.quantity){
								editor.jqxNumberInput('val', qoh);
							} else {
								editor.jqxNumberInput('val', data.quantity);
							}
					    },
					    validation: function (cell, value) {
					    	var data = grid.jqxGrid('getrowdata', cell.row);
					        if (value) {
					        	var requireAmount = data.requireAmount;
								var qoh = data.quantityOnHandTotal;
					   			if (requireAmount && requireAmount == 'Y') {
					   				qoh = data.amountOnHandTotal;
					   			}
								if (value > qoh){
									 return { result: false, message: '${uiLabelMap.NotEnoughDetail}'};
								}
					        }
					        return true;
					    },
					},
					{text: '${StringUtil.wrapString(uiLabelMap.Unit)}', dataField: 'quantityUomId', width: 150, columntype: 'dropdownlist',  filterable:false, editable: false,
						cellsrenderer: function(row, column, value){
							var data = grid.jqxGrid('getrowdata', row);
				   			var requireAmount = data.requireAmount;
				   			if (requireAmount && requireAmount == 'Y') {
				   				value = data.weightUomId;
				   			}
							return '<span title=' + getUomDescription(value) + '>' + getUomDescription(value) + '</span>';
						},
					},

					{ text: '${uiLabelMap.ATP}', datafield: 'availableToPromiseTotal', hidden: true, editable: false, align: 'left', width: 120, cellsalign: 'right', filtertype: 'number',
						cellsrenderer: function(row, colum, value){
							if(value){
								var data = $('#jqxgridItemAndLabel').jqxGrid('getrowdata', row);
								return '<span style=\"text-align: right;\">' + formatnumber(value) +'</span>';
							} else {
								return '<span style=\"text-align: right;\">0</span>';
							}
					    },
					    rendered: function(element){
					    	$(element).jqxTooltip({content: '${StringUtil.wrapString(uiLabelMap.AvailableToPromiseTotal)}', theme: 'orange' });
					    }, 
					},
					{ text: '${StringUtil.wrapString(uiLabelMap.ProductManufactureDate)}',editable: false, dataField: 'datetimeManufactured', align: 'left', width: 130, columntype: 'datetimeinput', filtertype: 'range', cellsformat: 'dd/MM/yyyy', cellsalign: 'right',
						cellsrenderer: function(row, colum, value){
							if(!value){
								return '<span style=\"text-align: right;\"></span>';
							}
					    },
					},
					{ text: '${StringUtil.wrapString(uiLabelMap.ProductExpireDate)}',editable: false, dataField: 'expireDate', align: 'left', width: 130, columntype: 'datetimeinput', filtertype: 'range', cellsformat: 'dd/MM/yyyy', cellsalign: 'right',
						cellsrenderer: function(row, colum, value){
							if(!value){
								return '<span style=\"text-align: right;\"></span>';
							}
					    },
					},
				"/>
	
	<@jqGrid filtersimplemode="true" filterable="true" dataField=dataFieldDetail columnlist=columnlistDetail editable="true" editmode="click" showtoolbar="false" selectionmode="checkbox"
		url="jqxGeneralServicer?sname=jqGetInventoryAndLabelDetail" id="jqxgridItemAndLabel" mouseRightMenu="false" jqGridMinimumLibEnable="true" initrowdetails="true" initrowdetailsDetail=initrowdetails
	/>
</div>
<script>
	$("#jqxgridItemAndLabel").on('rowselect', function (event) {
		var target = event.target;
		var divId = $(target).attr('id');
		if (divId == "jqxgridItemAndLabel" && allowSelected == false){
			var index = event.args.rowindex;
			var par = $('#jqxgridItemAndLabel').jqxGrid("getrowdata", index);
			var quantity = par.quantity;
			var listChilds = [];
			var check = false;
			if ($('#jqxgridItemAndLabel'+ index).length > 0){
				var childIndexs = $('#jqxgridItemAndLabel'+ index).jqxGrid('getselectedrowindexes');
				if (childIndexs.length > 0){
					check = true; 
				}	
			}
			if (!check){
				$('#jqxgridItemAndLabel').jqxGrid('unselectrow', index);
				$('#jqxgridItemAndLabel').jqxGrid('showrowdetails', index);
			}
			if (quantity === undefined || quantity === null || quantity === '' || quantity === 0){
				$("#jqxgridItemAndLabel").jqxGrid('begincelledit', index, "quantity");
			}
		} else if (divId == "jqxgridItemAndLabel" && allowSelected == true){
			var index = event.args.rowindex;
			var par = $('#jqxgridItemAndLabel').jqxGrid("getrowdata", index);
			var quantity = par.quantity;
			if (quantity === undefined || quantity === null || quantity === '' || quantity === 0){
				$("#jqxgridItemAndLabel").jqxGrid('begincelledit', index, "quantity");
			}
		}
		
		allowSelected = false;
		
	});
	
	$("#jqxgridItemAndLabel").on('rowunselect', function (event) {
		var target = event.target;
		var divId = $(target).attr('id');
		if (divId == "jqxgridItemAndLabel"){
			var index = event.args.rowindex;
			var id = $('#jqxgridItemAndLabel').jqxGrid('getrowid', index);
			$("#jqxgridItemAndLabel").jqxGrid('setcellvaluebyid', id, "quantity", 0);
		}
	});
</script>