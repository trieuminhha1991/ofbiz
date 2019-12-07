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
<#assign columnlist="{ text: '${uiLabelMap.description}', datafield: 'description', width:250,cellsrenderer : function(row){
							var data = $('#jqxgrid').jqxGrid('getrowdata',row);
							return '<span class=\"custom-style-word\">' + data.description + '</span>';
						}
					},
					{ text: '${uiLabelMap.FormFieldTitle_defaultGlAccountId}', datafield: 'accountCode', width:200},
					{ text: '${uiLabelMap.FormFieldTitle_overrideGlAccountId}', datafield: 'accountCodeOver', width:200},
					{ text: '${uiLabelMap.FormFieldTitle_activeGlDescription}', datafield: 'accountName'}
					"/>	
<@jqGrid url="jqxGeneralServicer?sname=JQGetListGLAccountItemTypeSale" filtersimplemode="true" dataField=dataField columnlist=columnlist showtoolbar="true" editable="false"
		 height="640"  addrefresh="true" sortable="false" alternativeAddPopup="alterpopupWindow" addrow="true" addType="popup" addrefresh="true"
		 id="jqxgrid" 
		 addColumns="invoiceItemTypeId;glAccountId;organizationPartyId[${userLogin.lastOrg}]" 
		 createUrl="jqxGeneralServicer?jqaction=C&sname=addInvoiceItemTypeGlAssignment"
		 removeUrl="jqxGeneralServicer?sname=removeInvoiceItemTypeGlAssignment&jqaction=D" deleterow="true" clearfilteringbutton="true" 
		 _customMessErr="OlbGlAccountSalesInvoices.customMessageError"
		 deleteColumn="invoiceItemTypeId;organizationPartyId[${userLogin.lastOrg}]" deleteConditionFunction="deleteConditionFunction"
		 jqGridMinimumLibEnable="false"
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