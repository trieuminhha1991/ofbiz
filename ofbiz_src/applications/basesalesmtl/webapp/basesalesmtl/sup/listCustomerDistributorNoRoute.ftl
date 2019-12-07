<#assign dataField="[{ name: 'customerPartyId', type: 'string'},
					{ name: 'customerPartyCode', type: 'string'},
					{ name: 'customerGroupName', type: 'string'},
					{ name: 'address', type: 'string'}]"/>
<#assign columnlist="{ text: '${StringUtil.wrapString(uiLabelMap.BSCustomerId)}', datafield: 'customerPartyId', width: 150},
					{ text: '${StringUtil.wrapString(uiLabelMap.BSCustomerName)}', datafield: 'customerGroupName', maxwidth: 200},
					{ text: '${StringUtil.wrapString(uiLabelMap.BSAddress)}', datafield: 'address', minwidth: 300,
						cellsrenderer: function(row, column, value, a, b, data){
							var str = '';
							if(value.address1){
								str += value.address1;
							}
							if(value.district){
								str += ' ' + value.district;
							}
							if(value.city){
								str +=  ' ' + value.city;
							}
							return '<div class=\"cell-custom-grid\">'+str+'</div>'
						}
					}"/>

<@jqGrid id="ListCustomerDistributorNoRoute" dataField=dataField columnlist=columnlist clearfilteringbutton="true" selectionmode="checkbox"
	showtoolbar="true" customLoadFunction="true" jqGridMinimumLibEnable="false" autoheight="false"  isShowTitleProperty="false"
	url="jqxGeneralServicer?sname=JQGetListCustomerDistributorNoRoute" height="300" sourceId="customerPartyId"
	customcontrol1="fa fa-map-marker@${uiLabelMap.BSAddCustomer}@javascript: void(0);@CustomerRoute.addCustomerToRoute()"
	/>
