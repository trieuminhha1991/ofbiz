<script>
	<#assign acctgTransTypes = delegator.findList("AcctgTransType", null, null, null, null, true) />
	var acctgTransTypesData =  [<#list acctgTransTypes as item>{<#assign description = StringUtil.wrapString(item.get("description", locale)?if_exists)>'acctgTransTypeId' : "${item.acctgTransTypeId?if_exists}",'description' : "${description}"},</#list>];
	<#assign glAccounts = delegator.findList("GlAccount", null, null, null, null, false) />
	var glAccountData = [
		<#list glAccounts as item>
			<#assign description = StringUtil.wrapString(item.accountCode?if_exists  + " - " + item.accountName?if_exists + "[" + item.glAccountId?if_exists + "]" ) />
			{
				'description' : '${description}',
				'glAccountId' : '${item.glAccountId}'
			},
		</#list>
	];
</script>
<#assign dataField="[{ name: 'acctgTransId', type: 'string' },
                 	{ name: 'acctgTransEntrySeqId', type: 'string' },
                 	{ name: 'glAccountId', type: 'string' },
                 	{ name: 'accountName', type: 'string' },
					{ name: 'partyId', type: 'string' },
					{ name: 'fullName', type: 'string' },
					{ name: 'firstNameFrom', type: 'string' },
					{ name: 'middleNameFrom', type: 'string' },
					{ name: 'lastNameFrom', type: 'string' },
					{ name: 'groupNameFrom', type: 'string' },
					{ name: 'productId', type: 'string' },
                 	{ name: 'organizationPartyId', type: 'string' },
					{ name: 'groupNameOrganization', type: 'string' },	
                 	{ name: 'firstNameTo', type: 'string' },
					{ name: 'middleNameTo', type: 'string' },
					{ name: 'lastNameTo', type: 'string' },
					{ name: 'groupNameTo', type: 'string' },
                 	{ name: 'amount', type: 'number' }
		 		 	]"/>
<#assign columnlist="{ text: '${uiLabelMap.acctgTransId}', dataField: 'acctgTransId', width: 150,
	 					cellsrenderer: function (row, column, value) {
	 						return '<span><a href=EditAccountingTransaction?&organizationPartyId=${parameters.organizationPartyId}&acctgTransId=' + value +'>' + value + '</a></span>'
	 					}
					 },
					 { text: '${uiLabelMap.acctgTransEntrySeqId}', dataField: 'acctgTransEntrySeqId', width: 150},
					 { text: '${uiLabelMap.FormFieldTitle_glAccountId}', dataField: 'glAccountId', width: 150,columntype: 'dropdownlist',filtertype: 'checkedlist',
					 	cellsrenderer: function (row, column, value, a, b, data) {
							return '<span><a href=GlAccountNavigate?glAccountId=' + value +'>' + data.accountName + '</a></span>'
	 					},
						createfilterwidget: function (column, columnElement, widget) {
							var filterBoxAdapter2 = new $.jqx.dataAdapter(acctgTransTypesData, {autobind: true});
				   			var uniqueRecords2 = filterBoxAdapter2._source;
							if(uniqueRecords2.length){
								var first = uniqueRecords2[0];
								if(typeof(first) != 'string')
								uniqueRecords2.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
							}								
			   				widget.jqxDropDownList({ theme: theme, source: uniqueRecords2, autoDropDownHeight: false, displayMember: 'description', valueMember: 'acctgTransTypeId', width: '200', dropDownWidth: 300, dropDownHeight: 300});	
				   		},
					 },
					 { text: '${uiLabelMap.organizationName}', dataField: 'fullName', width: 250},
					 { text: '${uiLabelMap.organizationPartyId}', dataField: 'groupNameOrganization', width: 250},
					 { text: '${uiLabelMap.ProductProductName}', dataField: 'productName',width: 150, 
					 	cellsrenderer: function (row, column, value, a, b, data) {
							var x = value;
							if(data.productName){
								x = data.productName	
							}
		 					return '<span><a href=viewprofile?organizationPartyId=' + value +'>' + x + '</a></span>'
		 				}
					 },
					 { text: '${uiLabelMap.amount}', dataField: 'amount', 
						 cellsrenderer: function(row, column, value){
							 var data = $('#jqxgrid').jqxGrid('getrowdata', row);
							 return '<span>' + formatcurrency(value, data.currencyUomId) + '</span>'
						 }
					 }
					 "/>

<@jqGridMinimumLib/>
		 		 	
<script type="text/javascript">
	//create theme
	$.jqx.theme = 'olbius';  
	theme = $.jqx.theme;  
	var outFilterCondition = "";
	
	var SearchAccountReconciliation = function(container){
		var str = "<div id='glAccountIdList' class='pull-right margin-top5'></div>";
		container.append(str);
		$('#glAccountIdList').jqxDropDownList({
			height: 24, 
			dropDownWidth: 400, 
			source: glAccountData,
			displayMember: "description", 
			valueMember: "glAccountId", 
			placeHolder: "${uiLabelMap.ChooseGlAccountId}",
			theme: theme
			});
		$("#glAccountIdList").on('change', function () {
			outFilterCondition = ChangeFilterCondition($(this).val());
			$('#jqxgrid').jqxGrid('updatebounddata');	 
		});
		$('#glAccountIdList').jqxDropDownList('selectedIndex', 0);
	};
	var ChangeFilterCondition = function(val){
		var str = "|OLBIUS|glAccountId"
			   	+ "|SUIBLO|" + val
				+ "|SUIBLO|" + "EQUAL"
				+ "|SUIBLO|" + "or";
		return str;
	};
</script>
<#include "contextmenu/glReconciliation.ftl"/>

<@jqGrid filtersimplemode="true" id="jqxgrid" usecurrencyfunction="true" addType="popup" autorowheight='true'
		 dataField=dataField columnlist=columnlist clearfilteringbutton="true" showtoolbar="true" filterable="true"
		 addrow="true" editable="false" customtoolbaraction="SearchAccountReconciliation"
		 url="jqxGeneralServicer?sname=JQListGlAccountReconciliation&organizationPartyId=${parameters.organizationPartyId}" 
		 autoload="false" mouseRightMenu="true" contextMenuId="contextMenu" selectionmode="checkbox" jqGridMinimumLibEnable="false"	 
	   />