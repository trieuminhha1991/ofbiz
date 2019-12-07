<#assign dataField="[
					 { name: 'year', type: 'string'},
					 { name: 'depreciation', type: 'number'},
					 { name: 'depreciationTotal', type: 'number'},
					 { name: 'nbv', type: 'number'}
					]"/>

<#assign columnlist=" {
                       text: '${StringUtil.wrapString(uiLabelMap.SequenceId)}', sortable: false, filterable: false, editable: false,
                       groupable: false, draggable: false, resizable: false,
                       datafield: '', columntype: 'number', width: 50,
                       cellsrenderer: function (row, column, value) {
                          return \"<div style=\'margin:4px;\'>\" + (value + 1) + \"</div>\";
                       }
                  	 },
					 { text: '${StringUtil.wrapString(uiLabelMap.Year)}', datafield: 'year'},
					 { text: '${StringUtil.wrapString(uiLabelMap.accDepreciation)}', datafield: 'depreciation', cellsrenderer:
						 	function(row, colum, value){						 	
					 		var data = $('#jqxgridFar').jqxGrid('getrowdata', row);
					 		return \"<span>\" + formatcurrency(data.depreciation,'${purchaseCostUomId}') + \"</span>\";
					 }},
					 { text: '${StringUtil.wrapString(uiLabelMap.accDepreciationTotal)}', datafield: 'depreciationTotal', cellsrenderer:
						 	function(row, colum, value){
					 		var data = $('#jqxgridFar').jqxGrid('getrowdata', row);
					 		return \"<span>\" + formatcurrency(data.depreciationTotal,'${purchaseCostUomId}') + \"</span>\";
					 }},
					 { text: '${StringUtil.wrapString(uiLabelMap.accNetBookValue)}', datafield: 'nbv', cellsrenderer:
						 	function(row, colum, value){
					 		var data = $('#jqxgridFar').jqxGrid('getrowdata', row);
					 		return \"<span>\" + formatcurrency(data.nbv,'${purchaseCostUomId}') + \"</span>\";
					 }}
					"/>
	
<@jqGrid  filtersimplemode="false" addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="true" showtoolbar="true"
		 addrow="false" deleterow="false" id="jqxgridFar" customTitleProperties="${StringUtil.wrapString(uiLabelMap.PageTitleFixedAssetDepreciationReport)}"
		 url="jqxGeneralServicer?sname=listFixedAssetDepreciationReportJqx&fixedAssetId=${parameters.fixedAssetId}" 		 
		 />