<script>
	//Prepare finAccountType data
	<#assign finAccountTypes = delegator.findList("FinAccountType", null, null, null, null, false)/>
	var finAccTypeData = [
		<#list finAccountTypes as item>
			{
				<#assign description = StringUtil.wrapString(item.get("description", locale)?if_exists) />
				'finAccountTypeId' : '${item.finAccountTypeId?if_exists}',
				'description' : '${description?if_exists}'
			},
		</#list>
	];
	//Prepare status data
	<#assign statuses = delegator.findList("StatusItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusTypeId", "FINACCT_STATUS"), null, null, null, false) />
	var statusData = new Array();
	<#list statuses as item>
		var row = {};		
		<#assign description = StringUtil.wrapString(item.get("description", locale)?if_exists) />
		row['statusId'] = '${item.statusId?if_exists}';
		row['description'] = '${description?if_exists}';
		statusData[${item_index?if_exists}] = row;
	</#list>
	
</script>
<#assign columnlist="{ text: '${uiLabelMap.FormFieldTitle_finAccountId}', dataField: 'finAccountId', width: '150',
						cellsrenderer: function(row, column, value){
							return '<span><a href=EditFinAccountRoles?finAccountId=' + value + '>' + value + '</a></span>'
							}
						},
					 { text: '${uiLabelMap.FormFieldTitle_finAccountType}',filtertype : 'checkedlist', dataField: 'finAccountTypeId', width: '150',
						cellsrenderer: function(row, column, value){
							for(i = 0; i < finAccTypeData.length; i++){
								if(finAccTypeData[i].finAccountTypeId == value){
									return '<span title=' + value + '>' + finAccTypeData[i].description + '</span>'
								}
							}
							return ;
						},createfilterwidget : function(row,column,widget){
							var filterBox = new $.jqx.dataAdapter(finAccTypeData,{autoBind : true});
							var records = filterBox.records;
							records.splice(0,0,'${StringUtil.wrapString(uiLabelMap.wgfilterselectallstring)}');
							widget.jqxDropDownList({displayMember : 'description',valueMember : 'finAccountTypeId',dropDownHeight : 200,source : records});
						}
					 },
					 { text: '${uiLabelMap.statusId}', dataField: 'statusId', width: '150',filtertype : 'checkedlist',
						 cellsrenderer: function(row, column, value){
							 for(i = 0; i < statusData.length; i++){
								 if(statusData[i].statusId == value){
									 return '<span title=' + value + '>' + statusData[i].description + '</span>'
								 }
							 }
							 return ;
						 },createfilterwidget : function(row,column,widget){
							var filterBox = new $.jqx.dataAdapter(statusData,{autoBind : true});
							var records = filterBox.records;
							records.splice(0,0,'${StringUtil.wrapString(uiLabelMap.wgfilterselectallstring)}');
							widget.jqxDropDownList({displayMember : 'description',valueMember : 'statusId',dropDownHeight : 200,source : records});
						}
					 },
					 { text: '${uiLabelMap.FormFieldTitle_finAccountName}', dataField: 'finAccountName', width: '150'},
					 { text: '${uiLabelMap.FormFieldTitle_finAccountCode}', dataField: 'finAccountCode', width: '150'},
					 { text: '${uiLabelMap.FormFieldTitle_finAccountPin}', dataField: 'finAccountPin', width: '150'},
					 { text: '${uiLabelMap.currencyUomId}', dataField: 'currencyUomId', width: '150'},
					 { text: '${uiLabelMap.organizationPartyId}', dataField: 'groupNameOrganization',width: '250'},
					 { text: '${uiLabelMap.FormFieldTitle_ownerPartyId}', dataField: 'fullName', width: '250'},
					 { text: '${uiLabelMap.fromDate}', dataField: 'fromDate', cellsformat: 'dd/MM/yyyy', width: '150',filtertype : 'range'},
					 { text: '${uiLabelMap.thruDate}', dataField: 'thruDate', cellsformat: 'dd/MM/yyyy', width: '150',filtertype : 'range'},
					 { text: '${uiLabelMap.isRefundable}', dataField: 'isRefundable', width: '150', hidden : true},
					 { text: '${uiLabelMap.replenishPaymentId}', dataField: 'replenishPaymentId', width: '150', hidden : true},
					 { text: '${uiLabelMap.replenishLevel}', dataField: 'replenishLevel', width: '150', hidden : true},
					 { text: '${uiLabelMap.FormFieldTitle_actualBalance}', dataField: 'actualBalance', width: '150',filtertype : 'number'},
					 { text: '${uiLabelMap.AccountingBillingAvailableBalance}', dataField: 'availableBalance', width: '150',filtertype : 'number'}
					"/>
<#assign dataField="[{ name: 'finAccountId', type: 'string' },
                 	{ name: 'finAccountTypeId', type: 'string' },
                 	{ name: 'statusId', type: 'string' },
					{ name: 'finAccountName', type: 'string' },
					{ name: 'finAccountCode', type: 'string' },
                 	{ name: 'finAccountPin', type: 'string' },
                 	{ name: 'currencyUomId', type: 'string' }, 
                 	{ name: 'organizationPartyId', type: 'string'},
                 	{ name: 'groupNameOrganization', type: 'string'},
                 	{ name: 'ownerPartyId', type: 'string'},
                 	{ name: 'fullName', type: 'string'},
                 	{ name: 'fromDate', type: 'date',other : 'Timestamp'},
                 	{ name: 'thruDate', type: 'date',other : 'Timestamp'},
                 	{ name: 'isRefundable', type: 'string'},
                 	{ name: 'replenishPaymentId', type: 'string'},
                 	{ name: 'replenishLevel', type: 'number'},
                 	{ name: 'actualBalance', type: 'number'},
                 	{ name: 'availableBalance', type: 'number'}
		 		 	]"/>
<@jqGrid filtersimplemode="true" addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="true" 
		 showtoolbar="true" addrow="false" filterable="true" alternativeAddPopup="alterpopupWindow" deleterow="true" editable="false" 
		 url="jqxGeneralServicer?sname=JQListBankAccount" id="jqxgrid" removeUrl="jqxGeneralServicer?sname=deleteFinAccount&jqaction=D" 
		 deleteColumn="finAccountId" autorowheight="true"
		/>