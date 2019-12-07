<#assign dataField="[{ name: 'workEffortId', type: 'string' },
					 { name: 'workEffortName', type: 'string' }
					 ]"/>

<#assign columnlist="{ text: '${uiLabelMap.CommonId}', datafield: 'workEffortId', width: 200,
						cellsrenderer: function(column, row, value){
		             		return '<span><a href=FindInductedAppl?workEffortId=' + value + '>' + value + '</a></span>';
		             	}
					 },
                     { text: '${uiLabelMap.workEffortName}', datafield: 'workEffortName'}
					 "/>

<@jqGrid addrow="false" addType="popup" id="jqxgrid" filtersimplemode="true" showtoolbar="true" clearfilteringbutton="true"
		 url="jqxGeneralServicer?sname=JQGetListRecruitmentProcess" dataField=dataField columnlist=columnlist
		 />