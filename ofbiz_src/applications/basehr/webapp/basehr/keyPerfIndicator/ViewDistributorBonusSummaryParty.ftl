<#include "script/ViewDistBonusSummaryPartyScript.ftl"/>
<style>
.yellowCell{
	background-color: yellow !important;
}
</style>
<#assign datafield = "[{name: 'salesBonusSummaryId', type: 'string'},
					   {name: 'partyId', type: 'string'},
					   {name: 'partyCode', type: 'string'},
					   {name: 'groupName', type: 'string'},
					   {name: 'turnoverTarget', type: 'number'},
					   {name: 'turnoverActual', type: 'number'},
					   {name: 'turnoverRatio', type: 'number'},
					   {name: 'skuRatio', type: 'number'},
					   {name: 'turnoverBonus', type: 'number'},
					   {name: 'skuBonus', type: 'number'},
					   {name: 'totalBonus', type: 'number'}
					  ]"/>
<script type="text/javascript">
<#assign columngrouplist = "{text: '${StringUtil.wrapString(uiLabelMap.BSMTurnover)}', name: 'Turnover', align: 'center'},
							{text: '${StringUtil.wrapString(uiLabelMap.DistributorBonusShort)}', name: 'Bonus', align: 'center'},"/>
<#assign columnlist = "{text: '${StringUtil.wrapString(uiLabelMap.DistributorCodeShort)}', datafield: 'partyCode', width: '10%', editable: false},
					   {text: '${StringUtil.wrapString(uiLabelMap.BSDistributorName)}', datafield: 'groupName', width: '20%', editable: false},
					   {text: '${StringUtil.wrapString(uiLabelMap.BSTarget)}', datafield: 'turnoverTarget', width: '13%', editable: false, columntype: 'numberinput',
							filtertype: 'number', columngroup: 'Turnover' ,
							cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
							   return '<span style=\"text-align: right\">' + formatcurrency(value) + '</span>';
						   	}
					   },
					   {text: '${StringUtil.wrapString(uiLabelMap.DAActual)}', datafield: 'turnoverActual', width: '13%', editable: false, columntype: 'numberinput',
							filtertype: 'number', columngroup: 'Turnover' ,
							cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
							   return '<span style=\"text-align: right\">' + formatcurrency(value) + '</span>';
						   	}
					   },
					   {text: '${StringUtil.wrapString(uiLabelMap.HrCommonRates)}', datafield: 'turnoverRatio', width: '10%', editable: false, columntype: 'numberinput',
							filtertype: 'number', columngroup: 'Turnover' ,
							cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
							   return '<span style=\"text-align: right\"><b>' + value + '%</b></span>';
						   	}
					   },
					   {text: '${StringUtil.wrapString(uiLabelMap.SKUCompletionPercent)}', datafield: 'skuRatio', width: '12%', editable: false, columntype: 'numberinput',
							filtertype: 'number',
							cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
							   return '<span style=\"text-align: right\">' + value + '%</span>';
						   	}
					   },
					   {text: '${StringUtil.wrapString(uiLabelMap.BSMTurnover)}', datafield: 'turnoverBonus', width: '12%', editable: false, columntype: 'numberinput',
							filtertype: 'number', columngroup: 'Bonus' ,
							cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
							   return '<span style=\"text-align: right\">' + formatcurrency(value) + '</span>';
						   	},
						   	cellclassname: function (row, column, value, data) {
							    return 'yellowCell';
							}
					   },
					   {text: '${StringUtil.wrapString(uiLabelMap.DistributorSKUBonusShort)}', datafield: 'skuBonus', width: '12%', editable: false, columntype: 'numberinput',
							filtertype: 'number', columngroup: 'Bonus' ,
							cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
							   return '<span style=\"text-align: right\">' + formatcurrency(value) + '</span>';
						   	},
						   	cellclassname: function (row, column, value, data) {
							    return 'yellowCell';
							}
					   },
					   {text: '${StringUtil.wrapString(uiLabelMap.DASum)}', datafield: 'totalBonus', width: '12%', editable: false, columntype: 'numberinput',
							filtertype: 'number', columngroup: 'Bonus' ,
							cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
							   return '<span style=\"text-align: right\"><b>' + formatcurrency(value) + '</b></span>';
						   	},
						   	cellclassname: function (row, column, value, data) {
							    return 'yellowCell';
							}
					   },
						"/>
</script>	
<#assign customcontrol1 = "icon-refresh open-sans@${StringUtil.wrapString(uiLabelMap.RefreshData)}@javascript: void(0);@distributorBonusParty.updateData()" >
<@jqGrid filtersimplemode="true" filterable="true" addrefresh="true" showtoolbar="true" dataField=datafield columnlist=columnlist  
		clearfilteringbutton="true" editable="true" 
		addrow="false" showlist="true"
		url="jqxGeneralServicer?sname=JQGetListDistributorBonusSummaryParty&salesBonusSummaryId=${salesBonusSummaryId}"
	 	jqGridMinimumLibEnable="false" 
	 	columngrouplist=columngrouplist
	 	customcontrol1=customcontrol1
	 	deleterow="true" removeUrl="jqxGeneralServicer?sname=deleteSalesBonusSummaryParty&jqaction=D" deleteColumn="salesBonusSummaryId;partyId"
	/>			  
<script type="text/javascript" src="/hrresources/js/keyPerfIndicator/ViewDistributorBonusSummaryParty.js"></script>	
	