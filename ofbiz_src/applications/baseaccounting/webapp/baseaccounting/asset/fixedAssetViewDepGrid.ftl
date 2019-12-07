<#assign dataField="[
		{name: 'fixedAssetDepId', type: 'string'},
		{ name: 'fixedAssetId', type: 'string' },
		{ name: 'usefulLives', type: 'number' },
		{ name: 'lifeDepAmount', type: 'number' },
		{ name: 'remainingValue', type: 'number' },
		{ name: 'monthlyDepRate', type: 'number' },
		{ name: 'monthlyDepAmount', type: 'number' },
		{ name: 'accumulatedDep', type: 'number' }
]"/>

<#assign columnlist="{ text: '${uiLabelMap.BACCFixedAssetDepId}',filterable : false, datafield: 'fixedAssetDepId', width: 150 },
      { text: '${uiLabelMap.BACCFixedAssetId}', dataField: 'fixedAssetId', width: 150 },
      { text: '${uiLabelMap.BACCUsefulLives}', dataField: 'usefulLives', width: 200 },
      { text: '${uiLabelMap.BACCMonthlyDepRate}', dataField: 'monthlyDepRate', width: 200 },
      { text: '${uiLabelMap.BACCMonthlyDepAmount}', dataField: 'monthlyDepAmount', width: 150,
    	  cellsrenderer: function(row, columns, value){
    		  var data = $('#jqxgridFADep').jqxGrid('getrowdata',row);
    		  return '<span>'+formatcurrency(value, data.uomId)+'</span>';
    	  }  
      },
      { text: '${uiLabelMap.BACCAccumulatedDep}', dataField: 'accumulatedDep', width: 150,
    	  cellsrenderer: function(row, columns, value){
    		  var data = $('#jqxgridFADep').jqxGrid('getrowdata',row);
    		  return '<span>'+formatcurrency(value, data.uomId)+'</span>';
    	  }  
      },
      { text: '${uiLabelMap.BACCSalvageValue}', width: 150, dataField:'remainingValue', width: 150,
		 cellsrenderer: function(row, columns, value){
 		  var data = $('#jqxgridFADep').jqxGrid('getrowdata',row);
 		  return '<span>'+formatcurrency(value, data.uomId)+'</span>';
 	  }
	 },
	 { text: '${uiLabelMap.BACCDepreciation}', dataField: 'lifeDepAmount',
		cellsrenderer: function(row, columns, value){
 		  var data = $('#jqxgridFADep').jqxGrid('getrowdata',row);
 		  return '<span>'+formatcurrency(value, data.uomId)+'</span>';
		}
	 }
  "/>

<@jqGrid id="jqxgridFADep" customTitleProperties="BACCDepreciation" filtersimplemode="true" filterable="false" editable="false" addType="popup" showtoolbar="true"
		 url="jqxGeneralServicer?sname=JqxGetListFixedAssetDeps&fixedAssetId=${parameters.fixedAssetId}" dataField=dataField columnlist=columnlist
		/>