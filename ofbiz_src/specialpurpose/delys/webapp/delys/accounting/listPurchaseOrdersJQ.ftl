<script>
	<#assign orderStatuses = delegator.findList("StatusItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusTypeId", "ORDER_STATUS"), null, null, null, false) />
	var orderData =  [
		<#list orderStatuses as item>
			{
				<#assign description = StringUtil.wrapString(item.get("description", locale)) />
				'statusId' : "${item.statusId?if_exists}",
				'description' : "${description}"
			},
		</#list>		
	];
	
	<#assign orderTypes = delegator.findList("OrderType", null, null, null, null, false) />
	var orderTypeData =  [
		<#list orderTypes as item>
			<#assign description = StringUtil.wrapString(item.get("description", locale))>
			{
				'orderTypeId' : "${item.orderTypeId?if_exists}",
				'description' : "${description}"
			},
		</#list>
	];
</script>

<@jqGridMinimumLib/>
<#assign columnlist="{ text: '${uiLabelMap.DAOrderId}', dataField: 'orderId', width: 150,
						cellsrenderer: function (row, column, value) {
							var data = $('#jqxgrid').jqxGrid('getrowdata', row);
        					return '<a style = \"margin-left: 10px\" href=' + 'orderView?orderId=' + data.orderId + '>' +  data.orderId + '</a>'
    					}
					  },
 					  { text: '${uiLabelMap.DACreateDate}',filtertype: 'range', dataField: 'orderDate', width: 200, cellsformat: 'dd/MM/yyyy'},
                      { text: '${uiLabelMap.DAOrderName}', dataField: 'orderName', width: 250},
                      { text: '${uiLabelMap.DACustomer}', dataField: 'fullName', width: 350
						 },
					  { text: '${uiLabelMap.CommonStatus}', datafield: 'statusId', width: 200, filtertype: 'checkedlist',
					  	createfilterwidget: function (column, columnElement, widget) {
					  		var sourceOrd =
						    {
						        localdata: orderData,
						        datatype: \"array\"
						    };
			   				var filterBoxAdapter = new $.jqx.dataAdapter(sourceOrd,
			                {
			                    autoBind: true
			                });
			                var uniqueRecords = filterBoxAdapter.records;
			   				uniqueRecords.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
			   				
        					widget.jqxDropDownList({selectedIndex: 0, source: uniqueRecords, displayMember: 'statusId', valueMember: 'statusId', dropDownWidth: 250,
        						renderer: function (index, label, value) {
                    				for(i = 0; i < orderData.length; i++){
										if(orderData[i].statusId==value){
											return '<span>' + orderData[i].description + '</span>'
										}
                    				}
                    			return value;
        					}});
							widget.jqxDropDownList('checkAll');
    					},
    					cellsrenderer: function (row, column, value) {
							for(i = 0; i < orderData.length; i++){
								if(value == orderData[i].statusId){
									return '<span>' + orderData[i].description + '</span>'
								}
							}
    					}
					   },
                      { text: '${uiLabelMap.CommonAmount}', dataField: 'grandTotal', width: 250 , filtertype: 'number',
                      	cellsrenderer : function(row, colum, value){
					 		var data = $('#jqxgrid').jqxGrid('getrowdata', row);
					 		return \"<span>\" + formatcurrency(data.grandTotal,data.currencyUom) + \"</span>\";
					 	}}					   
					 "/>
<#assign dataField="[{ name: 'orderId', type: 'string' },
                 	{ name: 'orderName', type: 'string' },
                 	{ name: 'orderDate', type: 'date', other:'Timestamp' },
					{ name: 'partyId', type: 'string' },
					{ name: 'fullName', type: 'string' },
					{ name: 'grandTotal', type: 'number' }, 
                 	{ name: 'statusId', type: 'string'},
                 	{ name: 'currencyUom', type: 'string'}                                          
		 		 	]"/>	
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdropdownlist.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>
<script src="/delys/images/js/generalUtils.js"></script>
<@jqGrid defaultSortColumn="orderDate" sortdirection="DESC" filtersimplemode="true" addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="true" showtoolbar="true" addrow="false" filterable="true" alternativeAddPopup="alterpopupWindow" editable="false" 
		 url="jqxGeneralServicer?sname=JQListPurchaseOrders" usecurrencyfunction="true"
		 />    
