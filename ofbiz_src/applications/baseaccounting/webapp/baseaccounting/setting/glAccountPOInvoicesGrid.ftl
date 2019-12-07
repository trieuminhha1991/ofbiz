<#assign dataField="[
					{ name: 'description', type: 'string' },
					 { name: 'defaultGlAccountId', type: 'string' },
					 { name: 'glAccountId', type: 'string' },
					 { name: 'accountName', type: 'string' },
					 { name: 'accountCode', type: 'string' },
					 { name: 'accountCodeOver', type: 'string' },
					 { name: 'invoiceItemTypeId', type: 'string' }
					 ]
					"/>
<#assign columnlist="{ text: '${uiLabelMap.BACCDescription}', datafield: 'description', width:250},
					 { text: '${uiLabelMap.FormFieldTitle_defaultGlAccountId}', datafield: 'accountCode', width:200},
					 { text: '${uiLabelMap.FormFieldTitle_overrideGlAccountId}', datafield: 'accountCodeOver', width:250},
					 { text: '${uiLabelMap.FormFieldTitle_activeGlDescription}', datafield: 'accountName'}
					"/>	
<@jqGrid url="jqxGeneralServicer?invItemTypePrefix=${parameters.invItemTypePrefix?default('PINV')}&sname=JQGetListGLAccountItemTypeSale" filtersimplemode="true" dataField=dataField columnlist=columnlist showtoolbar="true" editable="false"
		 height="640"  addrefresh="true" sortable="false" alternativeAddPopup="alterpopupWindow" addrow="true" addType="popup" addrefresh="true"
		 id="jqxgrid" addColumns="invoiceItemTypeId;glAccountId;organizationPartyId[${userLogin.lastOrg}]" createUrl="jqxGeneralServicer?jqaction=C&sname=addInvoiceItemTypeGlAssignment"
		 removeUrl="jqxGeneralServicer?sname=removeInvoiceItemTypeGlAssignment&jqaction=D" deleterow="true" clearfilteringbutton="true" _customMessErr="OlbGlAccountPOInvoices.customMessageError"
		 deleteColumn="invoiceItemTypeId;organizationPartyId[${userLogin.lastOrg}]"  deleteConditionFunction="deleteConditionFunction"
		 />

<script>
	var msg_custom = "${StringUtil.wrapString(uiLabelMap.BACCErrorMessageWhenDupLicate)}";
	var deleteConditionFunction = function(){
		var index = $("#jqxgrid").jqxGrid("getselectedrowindex");
		if(index == -1){
			return false;
		}
		var data = $("#jqxgrid").jqxGrid("getrowdata", index);
		if(!data.accountCodeOver){
			return false;
		}
		return true;
	};
</script>