 <#assign dataField = "[
 		{name : 'billingAccountId',type : 'string'},
 		{name : 'description',type : 'string'},
 		{name : 'externalAccountId',type : 'string'}
 ]"/>       
 <#assign columnlist = "
 		{text  : '${uiLabelMap.AccountingBillingAccountId}',datafield : 'billingAccountId',width : '25%',cellsrenderer : function(row,datafield,column){
 			var data = $(\"#jqxGridBillingAccount\").jqxGrid('getrowdata',row);
 			if(typeof(data) != 'undefined' && data.hasOwnProperty('billingAccountId')){
 				return \"<span><a href='javascript:void(0);' onclick='javascript:set_value(&#39;\" + data.billingAccountId + \"&#39;)'>\" + data.billingAccountId + \"</a></span>\";
 			}
 		}},
 		{text  : '${uiLabelMap.CommonDescription}',datafield : 'description',width : '40%'},
 		{text  : '${uiLabelMap.AccountingExternalAccountId}',datafield : 'externalAccountId'}
 " />
 <@jqGrid id="jqxGridBillingAccount" dataField=dataField columnlist=columnlist filterable="true" filtersimplemode="true" addrefresh="true" clearfilteringbutton="true" showtoolbar="true"
 	url="jqxGeneralServicer?sname=JQGetListBillingAccount"
   />
 <script>
 	//$('#jqxGridBillingAccount').jqxGrid({'width' : ($('#0_lookupId').width())});
 </script>
