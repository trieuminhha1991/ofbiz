<#--Import LIB-->
<#--/Import LIB-->
<#--===================================Prepare Data=====================================================-->
<script>
	//Prepare for Payment Method Type data
	<#assign paymentMethodTypeList = delegator.findList("PaymentMethodType", null, null, null, null, false) />
	paymentMethodTypeData = [
	              <#list paymentMethodTypeList as item>
					<#assign description = StringUtil.wrapString(item.description?if_exists) />
					{'paymentMethodTypeId': '${item.paymentMethodTypeId}', 'description': '${description}'},
				  </#list>
				];
	
	//Prepare for uom data
	<#assign statusList = delegator.findList("StatusItem", null, null, null, null, false)>
	var statusData = [
		<#list statusList as item>
			{
				<#assign description = StringUtil.wrapString(item.description) />
				statusId : '${item.statusId}',
				description : '${description}',
			},
		</#list>
	]
</script>
<#--===================================/Prepare Data=====================================================-->
<#--=================================Init Grid======================================================-->
<#assign dataField="[{ name: 'billingAccountId', type: 'string'},
					 { name: 'orderId', type: 'string'},
					 { name: 'orderDate', type: 'date', other: 'Timestamp'},
					 { name: 'paymentMethodTypeId', type: 'string'},
					 { name: 'paymentStatusId', type: 'string'},
					 { name: 'maxAmount', type: 'string'},
					 ]"/>

<#assign columnlist="{ text: '${uiLabelMap.FormFieldTitle_orderId}', datafield: 'orderId', editable: false, width: 150, editable: 'false',
						cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
							return '<span title=' + value + '><a href=orderView?orderId=' + value + '>' + value + '</a></span>';
						}
					 },
                     { text: '${uiLabelMap.FormFieldTitle_orderDate}', datafield: 'orderDate', width: 250, editable: 'false', cellsformat: 'dd/MM/yyyy'},
                     { text: '${uiLabelMap.paymentMethodTypeId}', datafield: 'paymentMethodTypeId', width: 250, editable: 'false', filtertype: 'checkedlist',
						 cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
 							for(var i = 0; i < paymentMethodTypeData.length; i++){
 								if(value == paymentMethodTypeData[i].paymentMethodTypeId){
 									return '<span title=' + value + '>' + paymentMethodTypeData[i].description + '</span>';
 								}
 							}
 							return '<span> ' + value + '</span>';
 						},
 						createfilterwidget: function (column, columnElement, widget) {
							var filterDataAdapter = new $.jqx.dataAdapter(paymentMethodTypeData, {
								autoBind: true
							});
							var records = filterDataAdapter.records;
							records.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
							widget.jqxDropDownList({source: records, displayMember: 'description', valueMember: 'paymentMethodTypeId',
								renderer: function(index, label, value){
									for(var i = 0; i < paymentMethodTypeData.length; i++){
										if(paymentMethodTypeData[i].paymentMethodTypeId == value){
											return '<span>' + paymentMethodTypeData[i].description + '</span>';
										}
									}
									return value;
								}
							});
							widget.jqxDropDownList('checkAll');
			   			}
                     },
                     { text: '${uiLabelMap.FormFieldTitle_paymentStatusId}', datafield: 'paymentStatusId', editable: 'false', filtertype: 'checkedlist',
                    	 cellsrenderer: function(row, column, value){
                    		 for(var i = 0; i < statusData.length; i++){
  								if(value == statusData[i].statusId){
  									return '<span title=' + value + '>' + statusData[i].description + '</span>';
  								}
  							}
  							return '<span> ' + value + '</span>'; 
						},
 						createfilterwidget: function (column, columnElement, widget) {
							var filterDataAdapter = new $.jqx.dataAdapter(statusData, {
								autoBind: true
							});
							var records = filterDataAdapter.records;
							records.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
							widget.jqxDropDownList({source: records, displayMember: 'description', valueMember: 'statusId',
								renderer: function(index, label, value){
									for(var i = 0; i < statusData.length; i++){
										if(statusData[i].statusId == value){
											return '<span>' + statusData[i].description + '</span>';
										}
									}
									return value;
								}
							});
							widget.jqxDropDownList('checkAll');
			   			}
                     }
					 "/>

<@jqGrid id="jqxgrid" filtersimplemode="true" addrow="false" addrefresh="true" editable="false" deleterow="false" addType="popup" alternativeAddPopup="wdwNewBillingAccRole" showtoolbar="true" clearfilteringbutton="true"
		 url="jqxGeneralServicer?sname=JQGetListBillingAccountOrders&billingAccountId=${parameters.billingAccountId}" dataField=dataField columnlist=columnlist
		 />
                     
<#--=================================/Init Grid======================================================-->
<#--====================================================/Setup JS======================================-->