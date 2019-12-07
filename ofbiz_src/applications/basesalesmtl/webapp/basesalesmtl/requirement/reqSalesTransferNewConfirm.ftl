<div style="position:relative">
	<div class="row-fluid">
		<div class="form-horizontal form-window-content-custom label-text-left content-description" style="margin:10px">
			<div class="row-fluid">
				<div class="span6">
					<div class="row-fluid">
						<div class="div-inline-block">
							<label>${uiLabelMap.RequirementType}:</label>
						</div>
						<div class="div-inline-block">
							<span id="strRequirementTypeId"></span>
						</div>
					</div>
					<div class="row-fluid">
						<div class="div-inline-block">
							<label>${uiLabelMap.ReasonRequirement}:</label>
						</div>
						<div class="div-inline-block">
							<span id="strReasonEnumId"></span>
						</div>
					</div>
					<div class="row-fluid hide">
						<div class="div-inline-block">
							<label>${uiLabelMap.BSRequiredByDate}:</label>
						</div>
						<div class="div-inline-block">
							<span id="strRequiredByDate"></span>
						</div>
					</div>
					<div class="row-fluid">
						<div class="div-inline-block">
							<label>${uiLabelMap.BSRequirementStartDate}:</label>
						</div>
						<div class="div-inline-block">
							<span id="strRequirementStartDate"></span>
						</div>
					</div>
				</div><!--.span6-->
				<div class="span6">
					<div class="row-fluid">
						<div class="div-inline-block">
							<label>${uiLabelMap.BSCustomer}:</label>
						</div>
						<div class="div-inline-block">
							<span id="strCustomerId"></span>
						</div>
					</div>
					<div class="row-fluid">
						<div class="div-inline-block">
							<label>${uiLabelMap.BSCustomerAddress}:</label>
						</div>
						<div class="div-inline-block">
							<span id="strCustomerContactMechId"></span>
						</div>
					</div>
					<div class="row-fluid facilityIdContainer">
						<div class="div-inline-block">
							<label>${uiLabelMap.FacilityFrom}:</label>
						</div>
						<div class="div-inline-block">
							<span id="strFacilityId"></span>
						</div>
					</div>
					<div class="row-fluid">
						<div class="div-inline-block">
							<label>${uiLabelMap.FacilityTo}:</label>
						</div>
						<div class="div-inline-block">
							<span id="strDestFacilityId"></span>
						</div>
					</div>
					<div class="row-fluid">
						<div class="div-inline-block">
							<label>${uiLabelMap.BSDescription}:</label>
						</div>
						<div class="div-inline-block">
							<span id="strDescription"></span>
						</div>
					</div>
				</div><!--.span6-->
			</div>
		</div><!-- .form-horizontal -->
	</div><!--.row-fluid-->
	<div class="row-fluid">
		<div class="span12">
			<div id="jqxgridProductSelected" style="width: 100%"></div>
		</div>
	</div>
</div>

<#assign dataFieldConfirms = "[
					{name: 'productId', type: 'string'},
					{name: 'productCode', type: 'string'},
					{name: 'parentProductId', type: 'string'},
					{name: 'productName', type: 'string'},
					{name: 'quantityUomId', type: 'string'},
					{name: 'packingUomIds', type: 'string'},
					{name: 'parentProductId', type: 'string'},
					{name: 'expiredDate', type: 'date', other: 'Timestamp'},
					{name: 'unitCost', type: 'number'},
					{name: 'quantity', type: 'number', formatter: 'integer'},
					{name: 'description', type: 'string'},
				]"/>
<#assign columnlistConfirm = "
				{text: '${uiLabelMap.SequenceId}', datafield: '', sortable: false, filterable: false, editable: false, pinned: true, groupable: false, draggable: false, resizable: false, columntype: 'number', width: 50,
				    cellsrenderer: function (row, column, value) {
				        return '<div style=margin:4px;>' + (value + 1) + '</div>';
				    }
				},
				{text: '${StringUtil.wrapString(uiLabelMap.ProductId)}', dataField: 'productCode', width: '20%', editable:false},
				{text: '${StringUtil.wrapString(uiLabelMap.ProductName)}', dataField: 'productName', editable: false, minwidth: 200},
				{text: '${StringUtil.wrapString(uiLabelMap.Unit)}', dataField: 'quantityUomId', width: '15%', columntype: 'dropdownlist',
					cellsrenderer: function(row, column, value){
						var data = $('#jqxgridProductSelected').jqxGrid('getrowdata', row);
							for (var i = 0 ; i < quantityUomData.length; i++){
							if (value == quantityUomData[i].uomId){
								return '<span title=' + quantityUomData[i].description + '>' + quantityUomData[i].description + '</span>';
							}
						}
						return value;
					}
				}, "/>
	<#assign columnlistConfirm = columnlistConfirm + "
				{text: '${StringUtil.wrapString(uiLabelMap.Quantity)}', dataField: 'quantity', width: '15%', cellsalign: 'right', filterable:false, sortable: false, columntype: 'numberinput', cellsformat: 'd',
					cellsrenderer: function(row, column, value) {
				 		var str = '<div class=\"innerGridCellContent align-right\">';
				 		if (value) str += formatnumber(value);
						str += '</div>';
						return str;
				 	}
				},
			"/>
    <#assign columnlistConfirm = columnlistConfirm + "
				{ text: '${uiLabelMap.ExpireDate}', dataField: 'expiredDate', columntype: 'datetimeinput', cellsformat: 'dd/MM/yyyy', width: 150, editable: true, sortable: false, filterable: false,
					cellsrenderer: function (row, column, value){
						if (value){
							return '<span style=\"text-align: right;\">' + DatetimeUtilObj.getFormattedDate(new Date(value)) + '</span>';
						}
						return '<span style=\"text-align: right;\">_NA_</span>';
					}
				},
			"/>
<#if displayDescription?exists && displayDescription = "Y">
	<#assign columnlistConfirm = columnlistConfirm + "{text: '${StringUtil.wrapString(uiLabelMap.CommonNote)}', dataField: 'description', editable: false, width: '15%'},"/>
</#if>

<@jqGrid filtersimplemode="true" id="jqxgridProductSelected" filterable="false" dataField=dataFieldConfirms columnlist=columnlistConfirm editable="false" showtoolbar="false"
		url="" editmode='click' selectionmode='multiplecellsadvanced'
	/>

<#--
{ text: '${StringUtil.wrapString(uiLabelMap.UnitPrice)}', dataField: 'unitCost', width: '12%', cellsalign: 'right', cellsformat: 'c', filterable:false, sortable: false, columntype: 'numberinput', 
 	cellsrenderer: function(row, column, value) {
 		var str = '<div class=\"innerGridCellContent align-right\">';
 		//var data = $('#listOrderCustomer').jqxGrid('getrowdata', row);
 		if (value) str += formatcurrency(value);
		str += '</div>';
		return str;
 	}
},
-->
