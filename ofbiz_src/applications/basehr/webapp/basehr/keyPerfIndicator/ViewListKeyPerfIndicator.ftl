<#include "script/ViewListKeyPerfIndicatorScript.ftl"/>
<#assign datafield = "[{name: 'keyPerfIndicatorId', type: 'string'},
					   {name: 'keyPerfIndicatorName', type: 'string'},
					   {name: 'description', type: 'string'},
					   {name: 'fromDate', type: 'date'},
					   {name: 'thruDate', type: 'date'},
					   {name: 'partyAppl', type: 'string'},
					   {name: 'positionTypeAppl', type: 'string'},
					   {name: 'isApplAllParty', type: 'string'},
					   {name: 'isApplAllPosType', type: 'string'},
					   ]"/>
					   
<script type="text/javascript">
<#assign columnlist = "{text: '${StringUtil.wrapString(uiLabelMap.KeyPerfIndicatorName)}', datafield: 'keyPerfIndicatorName', width: '20%'},
					   {text: '${StringUtil.wrapString(uiLabelMap.ApplyForParty)}', datafield: 'partyAppl', width: '27%',
						   cellsrenderer: function (row, column, value) {
							   var rowData = $('#jqxgrid').jqxGrid('getrowdata', row);
							   if(rowData && rowData.isApplAllParty == 'Y'){
								   return '<span>${StringUtil.wrapString(uiLabelMap.CommonAll)}</span>';
							   }
							   return '<span>' + value + '</span>';
						   }
					   },	   
					   {text: '${StringUtil.wrapString(uiLabelMap.ApplyForPositionType)}', datafield: 'positionTypeAppl', width: '27%',
						   cellsrenderer: function (row, column, value) {
							   var rowData = $('#jqxgrid').jqxGrid('getrowdata', row);
							   if(rowData && rowData.isApplAllPosType == 'Y'){
								   return '<span>${StringUtil.wrapString(uiLabelMap.CommonAll)}</span>';
							   }
							   return '<span>' + value + '</span>';
						   }   
					   },	   
					   {text: '${StringUtil.wrapString(uiLabelMap.EffectiveFromDate)}', datafield: 'fromDate', width: '13%', editable: false, 
						   cellsformat: 'dd/MM/yyyy', filterType : 'range'},
					   {text: '${StringUtil.wrapString(uiLabelMap.CommonThruDate)}', datafield: 'thruDate', width: '13%', editable: false, 
						   cellsformat: 'dd/MM/yyyy', filterType : 'range'},
					   "/>
</script>
<@jqGrid filtersimplemode="true" filterable="true" addrefresh="true" showtoolbar="true" dataField=datafield columnlist=columnlist  
	clearfilteringbutton="true" showlist="true" 
	addType="popup" alternativeAddPopup="AddNewKeyPerfIndicatorWindow" addrow="true" 
	editable="false" deleterow="false" 
	url="jqxGeneralServicer?sname=JQGetListKeyPerfIndicator" autorowheight="false" jqGridMinimumLibEnable="false"
 	updateUrl=""
	editColumns=""/>
	
<#include "AddKeyPerfIndicator.ftl"/>							   