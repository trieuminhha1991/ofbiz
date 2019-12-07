<div style="position:relative">
	<div class="row-fluid">
		<div class="form-horizontal form-window-content-custom label-text-left content-description" style="margin:10px">
			<div class="row-fluid">
				<div class="span6">
					<div class="row-fluid">
						<div class="div-inline-block">
							<label>${uiLabelMap.BSQuotationId}:</label>
						</div>
						<div class="div-inline-block">
							<span id="strProductQuotationId"></span>
						</div>
					</div>
					<div class="row-fluid">
						<div class="div-inline-block">
							<label>${uiLabelMap.BSQuotationName}:</label>
						</div>
						<div class="div-inline-block">
							<span id="strQuotationName"></span>
						</div>
					</div>
					<div class="row-fluid">
						<div class="div-inline-block">
							<label>${uiLabelMap.BSPSProductStoreGroup}:</label>
						</div>
						<div class="div-inline-block">
							<span id="strProductStoreGroupIds"></span>
						</div>
					</div>
					<div class="row-fluid">
						<div class="div-inline-block">
							<label>${uiLabelMap.BSPSSalesChannel}:</label>
						</div>
						<div class="div-inline-block">
							<span id="strProductStoreIds"></span>
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
				<div class="span6">
					<#--
					<div class="row-fluid">
						<div class="div-inline-block">
							<label>${uiLabelMap.BSPartyApply}:</label>
						</div>
						<div class="div-inline-block">
							<span id="strRoleTypeId"></span>
						</div>
					</div>
					-->
					<div class="row-fluid">
						<div class="div-inline-block">
							<label>${uiLabelMap.BSCurrencyUomId}:</label>
						</div>
						<div class="div-inline-block">
							<span id="strCurrencyUomId"></span>
						</div>
					</div>
					<div class="row-fluid">
						<div class="div-inline-block">
							<label>${uiLabelMap.BSFromDate}:</label>
						</div>
						<div class="div-inline-block">
							<span id="strFromDate"></span>
						</div>
					</div>
					<div class="row-fluid">
						<div class="div-inline-block">
							<label>${uiLabelMap.BSThruDate}:</label>
						</div>
						<div class="div-inline-block">
							<span id="strThruDate"></span>
						</div>
					</div>
					<div class='row-fluid'>
						<div class='div-inline-block'>
							<label>${uiLabelMap.BSCustomer}/${uiLabelMap.BSAbbCustomerGroup}:</label>
						</div>
						<div class="div-inline-block">
							<span id="strPartyId"></span>
				   		</div>
					</div>
				</div><!--.span6-->
			</div>
		</div><!-- .form-horizontal -->
	</div><!--.row-fluid-->
	<div class="row-fluid">
		<div class="span12">
			<div id="jqxgridProdSelected" style="width: 100%"></div>
			
			<#assign dataFieldItemsProdConfirm = "[
				{name: 'productId', type: 'string'},
				{name: 'productCode', type: 'string'},
	       		{name: 'parentProductId', type: 'string'},
	       		{name: 'parentProductCode', type: 'string'},
	       		{name: 'features', type: 'string'},
	       		{name: 'productName', type: 'string'},
	       		{name: 'quantityConvert', type: 'string'},
	       		{name: 'quantityUomId', type: 'string'},
	       		{name: 'packingUomIds', type: 'array'},
	       		{name: 'taxPercentage', type: 'number'}, 
	       		{name: 'listPrice', type: 'number', formatter: 'float'},
	       		{name: 'listPriceVAT', type: 'number', formatter: 'float'},
	       		{name: 'colorCode', type: 'string'},
	       		{name: 'currencyUomId', type: 'string'},
	       		{name: 'unitPrice', type: 'number'},
	       		{name: 'unitPriceBef', type: 'number'},
	       		{name: 'unitPriceVAT', type: 'number'},
	    	]"/>
			<#assign columnListItemsProdConfirm = "[
				{text: '${StringUtil.wrapString(uiLabelMap.BSProductId)}', dataField: 'productCode', width: '14%', editable:false},
				{text: '${StringUtil.wrapString(uiLabelMap.BSProductName)}', dataField: 'productName', editable:false},
				{text: '${StringUtil.wrapString(uiLabelMap.BSTax)}', dataField: 'taxPercentage', editable:false, filterable: false, width: '7%', cellsalign: 'right', cellsformat: 'p'},
				{text: '${StringUtil.wrapString(uiLabelMap.BSProductPackingUomId)}', dataField: 'quantityUomId', width: '10%', editable:true, filterable: false, 
					cellsrenderer: function(row, column, value){
						var data = $('#jqxgridProdSelected').jqxGrid('getrowdata', row);
				 		var returnVal = '<div class=\"innerGridCellContent align-center\">';
			   			for (var i = 0 ; i < uomData.length; i++){
							if (value == uomData[i].uomId){
								returnVal += uomData[i].description + '</div>';
		   						return returnVal;
							}
						}
			   			returnVal += value + '</div>';
		   				return returnVal;
					}
				},
				{text: '${StringUtil.wrapString(uiLabelMap.BSPrice)} ${StringUtil.wrapString(uiLabelMap.BSBeforeVAT)}', dataField: 'listPrice', width: '14%', cellsalign: 'right',
					filterable:false, sortable:false, cellsformat: 'c', 
				 	cellsrenderer: function(row, column, value){
				 		var data = $('#jqxgridProdSelected').jqxGrid('getrowdata', row);
				 		var currencyUomId = $('#currencyUomId').val();
				 		var returnVal = '<div class=\"innerGridCellContent align-right\">';
			   			returnVal += formatcurrency(value, currencyUomId) + '</div>';
		   				return returnVal;
				 	},
				 	validation: function (cell, value) {
						if (value < 0) {
							return {result: false, message: '${uiLabelMap.BSQuantityMustBeGreaterThanZero}'};
						}
						return true;
					}
				},
				{text: '${StringUtil.wrapString(uiLabelMap.BSPrice)} ${StringUtil.wrapString(uiLabelMap.BSAfterVAT)}', dataField: 'listPriceVAT', width: '14%', cellsalign: 'right', editable: false,
					filterable:false, sortable:false, cellsformat: 'c', 
					cellsrenderer: function(row, column, value){
				 		var data = $('#jqxgridProdSelected').jqxGrid('getrowdata', row);
				 		var currencyUomId = $('#currencyUomId').val();
				 		var returnVal = '<div class=\"innerGridCellContent align-right\">';
				 		if (OlbCore.isNotEmpty(value)) {
				 			value = Math.round(value * 100) / 100;
				 		}
			   			returnVal += formatcurrency(value, currencyUomId) + '</div>';
		   				return returnVal;
				 	},
				},
			]"/>
		</div>
	</div>
</div>

<script type="text/javascript">
	var dataFieldItemsProdConfirm = ${StringUtil.wrapString(dataFieldItemsProdConfirm?default("[]"))};
	var columnListItemsProdConfirm = ${StringUtil.wrapString(columnListItemsProdConfirm?default("[]"))};
</script>
<script type="text/javascript" src="/salesresources/js/quotation/quotationNewConfirm.js"></script>
