<#assign uomList = delegator.findByAnd("Uom", {"uomTypeId" : "PRODUCT_PACKING"}, null, false)!/>
<#assign salesMethodChannelEnum = Static["com.olbius.basesales.util.SalesUtil"].getListSalesMethodChannelEnum(delegator)!/>
<script type="text/javascript">
	var uomData = [
	<#if uomList?exists>
		<#list uomList as uomItem>
			{	'uomId': '${uomItem.uomId}',
				'description': '${StringUtil.wrapString(uomItem.get("description", locale)?default(""))}'
			},
		</#list>
	</#if>
	];
	var salesMethodChannelEnumData = [
	<#if salesMethodChannelEnum?exists>
		<#list salesMethodChannelEnum as enumItem>
		{	enumId: '${enumItem.enumId}',
			description: '${StringUtil.wrapString(enumItem.get("description", locale))}'
		},
		</#list>
	</#if>
	];
	
	var cellClassProdPriceRule = function(row, columnfield, value) {
 		var data = $('#jqxgridQuotationItems').jqxGrid('getrowdata', row);
 		var returnValue = "";
 		if (typeof(data) != 'undefined') {
 			var now = new Date();
			if (data.thruDate != null && data.thruDate < now) {
				return "background-cancel";
			} else if (data.fromDate >= now) {
				return "background-prepare";
			}
 		}
    }
</script>
<div class="row-fluid">
	<div class="span12">
		<#assign dataField = "[
					{name: 'productQuotationId', type: 'string'},
					{name: 'quotationName', type: 'string'},
					{name: 'storeNames', type: 'string'},
					{name: 'productId', type: 'string'},
					{name: 'productCode', type: 'string'},
               		{name: 'productName', type: 'string'},
               		{name: 'currencyUomId', type: 'string'},
               		{name: 'quantityUomId', type: 'string'},
               		{name: 'taxPercentage', type: 'number'}, 
               		{name: 'listPrice', type: 'number', formatter: 'float'},
               		{name: 'listPriceVAT', type: 'number', formatter: 'float'},
               		{name: 'fromDate', type: 'date', other: 'Timestamp'},
               		{name: 'thruDate', type: 'date', other: 'Timestamp'},
            	]"/>
		<#assign columnlist = "
					{text: '${uiLabelMap.BPOSequenceId}', datafield: '', sortable: false, pinned: true, draggable: false, resizable: false, width: 50, cellclassname: cellClassProdPriceRule,
					    cellsrenderer: function (row, column, value) {
					        return '<div style=\"margin:4px;\">' + (row + 1) + '</div>';
					    }
					},"/>
		<#if hasOlbPermission("MODULE", "PRODQUOTATION_VIEW", "")>
		<#assign columnlist = columnlist + "
					{text: '${uiLabelMap.BSQuotationId}', dataField: 'productQuotationId', width: '8%', cellclassname: cellClassProdPriceRule,
						cellsrenderer: function(row, colum, value) {
							return '<span><a href=\"viewQuotation?productQuotationId=' + value + '\" target=\"_blank\">' + value + '</a></span>';
						}
					},"/>
		<#else>
		<#assign columnlist = columnlist + "
					{text: '${uiLabelMap.BSQuotationId}', dataField: 'productQuotationId', width: '8%', cellclassname: cellClassProdPriceRule},
					"/>
		</#if>
		<#assign columnlist = columnlist + "
					{text: '${uiLabelMap.BSQuotationName}', dataField: 'quotationName', cellclassname: cellClassProdPriceRule},
					{text: '${uiLabelMap.BSPSSalesChannel}', dataField: 'storeNames', cellclassname: cellClassProdPriceRule},
					{ text: '${StringUtil.wrapString(uiLabelMap.ProductPrice)} ${uiLabelMap.BSBeforeVAT}', datafield: 'listPriceVAT', columntype: 'numberinput', width: 120, cellclassname: cellClassProdPriceRule,
						cellsrenderer: function (row, column, value) {
					        return '<div class=\"text-right\">' + value.toLocaleString(locale) + '</div>';
					    }
					},
					{ text: '${StringUtil.wrapString(uiLabelMap.ProductCurrencyUom)}', datafield: 'currencyUomId', columntype: 'dropdownlist', filtertype: 'checkedlist', width: 100, cellclassname: cellClassProdPriceRule},
					{ text: '${StringUtil.wrapString(uiLabelMap.BSProductPackingUomId)}', datafield: 'quantityUomId', columntype: 'dropdownlist', filtertype: 'checkedlist', width: 110, cellclassname: cellClassProdPriceRule,
						cellsrenderer: function(row, colum, value){
							var returnValue = value;
							if (value) returnValue = quantityUomMap[value];
					       	return '<span>' + returnValue + '</span>';
					   	}
					},
					{ text: '${uiLabelMap.DmsFromDate}', datafield: 'fromDate', width: 150, cellclassname: cellClassProdPriceRule,
						cellsrenderer: function(row, colum, value) {
							return \"<span>\" + jOlbUtil.dateTime.formatFullDate(value) + \"</span>\";
						}
					},
					{ text: '${uiLabelMap.DmsThruDate}', datafield: 'thruDate', width: 150, cellclassname: cellClassProdPriceRule,
						cellsrenderer: function(row, colum, value) {
							return \"<span>\" + jOlbUtil.dateTime.formatFullDate(value) + \"</span>\";
						}
					}
				"/>
		<@jqGrid id="jqxgridQuotationItems" clearfilteringbutton="false" editable="false" filterable="false" columnlist=columnlist dataField=dataField
				viewSize="5" showtoolbar="true" editmode="click" selectionmode="singlerow" customTitleProperties="${uiLabelMap.BSPriceRule}" 
				url="jqxGeneralServicer?sname=JQGetListProductQuotationRulesByProduct&productId=${product.productId?if_exists}" enabletooltips="true"/>
	</div>
</div>