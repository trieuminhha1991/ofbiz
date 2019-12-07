<#assign status = delegator.findOne("StatusItem",Static["org.ofbiz.base.util.UtilMisc"].toMap("statusId","PARTY_ENABLED"),false) !>
<script>
	var stt = "${status.get("description",locale)?if_exists}";
	$(document).ready(function(){
		stt ? stt : '';
	})
</script>
<#assign dataField="[{ name: 'partyId', type: 'string' },
					 { name: 'partyTypeId', type: 'string'},
					 { name: 'fullName', type: 'string'},
					 { name: 'address1', type: 'string'},
					 { name: 'city', type: 'string'},
					 { name: 'statusId', type: 'string'},
					 { name: 'createdDate', type: 'date',other : 'Timestamp'}
					 ]
					 "/>
					 
<#assign columnlist="
	{ text: '${uiLabelMap.DACustomerId}', width:150, datafield: 'partyId'},
	{ text: '${uiLabelMap.DAFullName}', width:200, datafield: 'fullName'},
	{ text: '${StringUtil.wrapString(uiLabelMap.DAAddress)}', datafield: 'address1'},
	{ text: '${uiLabelMap.FormFieldTitle_city}', datafield: 'city',width : 150},
	{ text: '${uiLabelMap.FormFieldTitle_createdDate}', datafield: 'createdDate',cellsformat : 'dd/MM/yyyy',filtertype : 'range',width : 150},
	{ text: '${uiLabelMap.DAStatusId}', datafield: 'statusId',filterable : false,width : 150},
"	/>			

<@jqGrid url="jqxGeneralServicer?sname=getListCustomerOfSalesAdmGT" dataField=dataField columnlist=columnlist filterable="true" filtersimplemode="true" 
		 id="listCustomer" 
		 clearfilteringbutton="true" autorowheight="true" />	 