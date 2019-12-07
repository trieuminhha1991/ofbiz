<#assign dataField = "[
			{name: 'productCategoryId', type: 'string' },
			{name: 'productCategoryTypeId', type: 'string' },
			{name: 'primaryParentCategoryId', type: 'string' },
			{name: 'prodCatalogId', type: 'string' },
			{name: 'categoryName', type: 'string' },
			{name: 'longDescription', type: 'string' },
			{name: 'sequenceNum', type: 'number' },
			{name: 'fromDate', type: 'date', other: 'Timestamp'}
		]"/>
<#assign columnlist = "
			{text: '${uiLabelMap.DmsCategoryId}', dataField: 'productCategoryId', width: 150, editable: false,
				cellsrenderer: function(row, colum, value) {
			    	return '<span><a href=\"viewCategoryTax?productCategoryId=' + value + '\">' + value + '</a></span>';
			    }
			},
			{text: '${uiLabelMap.BSOfCatalog}', dataField: 'prodCatalogId', width: 150, editable: false},
			{text: '${uiLabelMap.DmsCategoryName}', dataField: 'categoryName', width: 250},
			{text: '${uiLabelMap.DmsDescription}', dataField: 'longDescription'},
			{text: '${uiLabelMap.BSSequenceNumber}', datafield: 'sequenceNum', width: 100}
  		"/>

<@jqGrid id="jqxListCategoryTax" url="jqxGeneralServicer?sname=JQListProductCategoryTax" columnlist=columnlist dataField=dataField 
		viewSize="15" showtoolbar="true" filtersimplemode="true" showstatusbar="false"/>

