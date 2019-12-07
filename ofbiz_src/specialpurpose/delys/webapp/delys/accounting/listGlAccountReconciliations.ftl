<script type="text/javascript">
	<#assign glAccounts = delegator.findList("GlAccount", null, null, null, null, false) />
	var glAccountData = new Array();
	<#list glAccounts as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.accountName?if_exists + "[" + item.accountCode?if_exists + "]") />
		row['glAccountId'] = '${item.glAccountId?if_exists}';
		row['description'] = '${description?if_exists}';
		glAccountData[${item_index}] = row;
	</#list>
</script>
<#assign dataField="[{ name: 'glReconciliationId', type: 'string' },
					 { name: 'glReconciliationName', type: 'string'},
					 { name: 'description', type: 'string'},
					 { name: 'createdByUserLogin', type: 'string'},
					 { name: 'lastModifiedByUserLogin', type: 'string'},
					 { name: 'glAccountId', type: 'string'},
					 { name: 'organizationPartyId', type: 'string'},
					 { name: 'reconciledDate', type: 'date', other:'Timestamp'},
					 ]
					 "/>
<#assign columnlist="{ text: '${uiLabelMap.FormFieldTitle_glReconciliationId}', datafield: 'glReconciliationId', filterable: false, width: 150,
						cellsrenderer: function (row, column, value){
							return '<span> <a href=' + 'EditGlReconciliation?glReconciliationId='+ value + '&organizationPartyId=company' + '>' + value + '</a></span>'
						}
					 },
					 { text: '${uiLabelMap.FormFieldTitle_glReconciliationName}', datafield: 'glReconciliationName', filterable: false, width: 150},
					 { text: '${uiLabelMap.description}', datafield: 'description', filterable: false, width: 150},
					 { text: '${uiLabelMap.FormFieldTitle_createdByUserLogin}', datafield: 'createdByUserLogin', filterable: false, width: 150},
					 { text: '${uiLabelMap.FormFieldTitle_lastModifiedByUserLogin}', datafield: 'lastModifiedByUserLogin', filterable: false, width: 150},
					 { text: '${uiLabelMap.FormFieldTitle_glAccountId}', datafield: 'glAccountId', width: 150,
						 cellsrenderer: function(row, column, value){
							 for(var i = 0; i < glAccountData.length; i++){
								 if(glAccountData[i].glAccountId == value){
									 return '<span>' + glAccountData[i].description + '</span>'
								 }
							 }
						 }
					 },
					 { text: '${uiLabelMap.FormFieldTitle_reconciledDate}', datafield: 'reconciledDate', width: 150, cellsformat: 'dd/MM/yyyy', filtertype: 'range'},
					 { text: '${uiLabelMap.FormFieldTitle_organizationPartyId}', datafield: 'organizationPartyId', filterable: false, width: 150},
					 { text: '${uiLabelMap.FormFieldTitle_reconciledBalance}', datafield: 'reconciledBalance', filterable: false}
					 "
					 />
<@jqGrid url="jqxGeneralServicer?sname=JQListGlReconciliation" dataField=dataField columnlist=columnlist id="jqxgrid" jqGridMinimumLibEnable="true"/>