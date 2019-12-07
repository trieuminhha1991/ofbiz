<#include "script/listInventoryAndLabelScript.ftl"/>
<div id="detailItems">
	<#assign dataFieldDetail="[
					{ name: 'productId', type: 'string'},
					{ name: 'productCode', type: 'string' },
					{ name: 'productName', type: 'string' },
					{ name: 'facilityId', type: 'string'},
					{ name: 'facilityName', type: 'string'},
	                { name: 'datetimeManufactured', type: 'date', other: 'Timestamp'},
	                { name: 'expireDate', type: 'date', other: 'Timestamp'},
					{ name: 'quantityOnHandTotal', type: 'number' },
					{ name: 'amountOnHandTotal', type: 'number' },
					{ name: 'availableToPromiseTotal', type: 'number' },
					{ name: 'quantityUomId', type: 'string' },
					{ name: 'weightUomId', type: 'string' },
					{ name: 'statusId', type: 'string' },
					{ name: 'statusDesc', type: 'string' },
					{ name: 'inventoryItemLabelId', type: 'string' },
					{ name: 'description', type: 'string'},
					{ name: 'requireAmount', type: 'string'},
					{ name: 'lotId', type: 'string' }]"/>
	<#assign columnlistDetail="
					{ text: '${uiLabelMap.SequenceId}', sortable: false, filterable: false, editable: false, pinned: true,
					    groupable: false, draggable: false, resizable: false,
					    datafield: '', columntype: 'number', width: 50,
					    cellsrenderer: function (row, column, value) {
					        return '<div style=margin:4px;>' + (value + 1) + '</div>';
					    }
					},
					{ text: '${uiLabelMap.Facility}', datafield: 'facilityName', align: 'left', width: 150, pinned: true},
					{ text: '${uiLabelMap.ProductId}', datafield: 'productCode', align: 'left', width: 150, pinned: true},
					{ text: '${uiLabelMap.ProductName}', datafield: 'productName', align: 'left', width: 250},
					{ text: '${uiLabelMap.ProductLabel}', datafield: 'description', align: 'left', minwidth: 200, filterable: true,
						cellsrenderer: function(row, colum, value){
							if(value === null || value === undefined || value === ''){
								return '<span></span>';
							}
					    }, 
					},
					{ text: '${uiLabelMap.QOH}', datafield: 'quantityOnHandTotal', align: 'left', width: 120, cellsalign: 'right', filtertype: 'number',
						cellsrenderer: function(row, colum, value){
							if(value){
								var data = $('#jqxgridItemAndLabel').jqxGrid('getrowdata', row);
								var requireAmount = data.requireAmount;
								if (requireAmount && requireAmount == 'Y') {
								 	value = data.amountOnHandTotal;
								}
								return '<span style=\"text-align: right;\">' + formatnumber(value) + '</span>';
							} else {
								return '<span style=\"text-align: right;\">0</span>';
							}
					    }, 
					    rendered: function(element){
					    	$(element).jqxTooltip({content: '${StringUtil.wrapString(uiLabelMap.QuantityOnHandTotal)}', theme: 'orange' });
					    }, 
					},
					{ text: '${uiLabelMap.ATP}', datafield: 'availableToPromiseTotal', hidden: true, align: 'left', width: 120, cellsalign: 'right', filtertype: 'number',
						cellsrenderer: function(row, colum, value){
							if(value){
								var data = $('#jqxgridItemAndLabel').jqxGrid('getrowdata', row);
								var des = null;
								for (var i = 0; i < uomData.length; i ++){
									if (data.quantityUomId == uomData[i].uomId){
										des = uomData[i].description;
									}
								}
								return '<span style=\"text-align: right;\">' + formatnumber(value) + '</span>';
							} else {
								return '<span style=\"text-align: right;\">0</span>';
							}
					    },
					    rendered: function(element){
					    	$(element).jqxTooltip({content: '${StringUtil.wrapString(uiLabelMap.AvailableToPromiseTotal)}', theme: 'orange' });
					    }, 
					},
					{ text: '${uiLabelMap.Unit}', datafield: 'quantityUomId', align: 'left', width: 120, filtertype: 'checkedlist', filterable: false,
						cellsrenderer: function (row, column, value){
							var data = $('#jqxgridItemAndLabel').jqxGrid('getrowdata', row);
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
						        	if (uomData.length > 0) {
										for(var i = 0; i < uomData.length; i++){
											if(uomData[i].uomId == value){
												return '<span>' + uomData[i].description + '</span>';
											}
										}
									}
									return value;
								}
							});
							widget.jqxDropDownList('checkAll');
			   			}
					},
					{ text: '${StringUtil.wrapString(uiLabelMap.ProductManufactureDate)}', dataField: 'datetimeManufactured', align: 'left', width: 130, columntype: 'datetimeinput', filtertype: 'range', cellsformat: 'dd/MM/yyyy', cellsalign: 'right',
						cellsrenderer: function(row, colum, value){
							if(!value){
								return '<span style=\"text-align: right;\"></span>';
							}
					    },
					},
					{ text: '${StringUtil.wrapString(uiLabelMap.ProductExpireDate)}', dataField: 'expireDate', align: 'left', width: 130, columntype: 'datetimeinput', filtertype: 'range', cellsformat: 'dd/MM/yyyy', cellsalign: 'right',
						cellsrenderer: function(row, colum, value){
							if(!value){
								return '<span style=\"text-align: right;\"></span>';
							}
					    },
					},
				"/>
<#if hasOlbPermission("MODULE", "LOG_INVENTORY", "ADMIN")>	
	<@jqGrid filtersimplemode="true" filterable="true" dataField=dataFieldDetail columnlist=columnlistDetail editable="false" showtoolbar="true"
		url="jqxGeneralServicer?sname=jqGetInventoryAndLabelDetail" customTitleProperties="ListInventoryAndLabel" id="jqxgridItemAndLabel"  mouseRightMenu="true" jqGridMinimumLibEnable="true" contextMenuId="contextMenu"
		customcontrol1="fa fa-plus@${uiLabelMap.AssignLabel}@javascript:InvAndLabelObj.prepareAssignLabelInventory();"
	/>
<#else>
	<@jqGrid filtersimplemode="true" filterable="true" dataField=dataFieldDetail columnlist=columnlistDetail editable="false" showtoolbar="true"
		url="jqxGeneralServicer?sname=jqGetInventoryAndLabelDetail" customTitleProperties="ListInventoryAndLabel" id="jqxgridItemAndLabel"  mouseRightMenu="true" jqGridMinimumLibEnable="true" contextMenuId="contextMenu"
	/>			
</#if>
</div>
<div id='contextMenu' style="display:none;">
	<ul>
		<li><i class="fa fa-refresh"></i>${StringUtil.wrapString(uiLabelMap.BSRefresh)}</li>
	</ul>
</div>
<div id="editpopupWindow" class="hide popup-bound">
	<div>${uiLabelMap.AssignLabel}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<input id="inventoryItemId" type="hidden"></input>
			<div class='row-fluid margin-top10'>
				<div class='span4' style="text-align: right">
					<span class="asterisk">${uiLabelMap.ProductLabel}</span>
				</div>
				<div class="span7">
					<div class="green-label" id="inventoryItemLabelId" name="inventoryItemLabelId"></div>
		   		</div>
			</div>
		</div>
		<div class="form-action popup-footer">
	        <button id="editCancel" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
	    	<button id="editSave" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
		</div>
	</div>
</div>