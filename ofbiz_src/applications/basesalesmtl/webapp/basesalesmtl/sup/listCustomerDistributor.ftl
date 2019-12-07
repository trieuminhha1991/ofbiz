<#assign dataField="[{ name: 'partyIdTo', type: 'string'},
					{ name: 'partyIdFrom', type: 'string'},
					{ name: 'disGroupName', type: 'string'},
					{ name: 'address', type: 'string'},
					{ name: 'latitude', type: 'number'},
					{ name: 'longitude', type: 'number'}]"/>
<#assign columnlist="{ text: '${StringUtil.wrapString(uiLabelMap.BSCustomerId)}', datafield: 'partyIdTo', width: 150, cellclassname : CustomerRoute.customClassCell},
					{ text: '${StringUtil.wrapString(uiLabelMap.BSCustomerName)}', datafield: 'disGroupName', maxwidth: 200, cellclassname : CustomerRoute.customClassCell},
					{ text: '${StringUtil.wrapString(uiLabelMap.BSAddress)}', datafield: 'address', minwidth: 300,cellclassname : CustomerRoute.customClassCell, filterable: false,
						cellsrenderer: function(row, column, value, a, b, data){
							var str = '';
							if(value.address1){
								str += value.address1;
							}
							if(value.wardGeoName){
								str += ' ' + value.wardGeoName;
							}
							if(value.districtGeoName){
								str += ' ' + value.districtGeoName;
							}
							if(value.stateProvinceGeoName){
								str +=  ' ' + value.stateProvinceGeoName;
							}
							return '<div class=\"cell-custom-grid\">'+str+'</div>';
						}
					}"/>

<@jqGrid id="ListCustomerDistributor" dataField=dataField columnlist=columnlist clearfilteringbutton="true" isShowTitleProperty="false"
	showtoolbar="true" customLoadFunction="true" jqGridMinimumLibEnable="false" autoheight="false"
	url="jqxGeneralServicer?sname=JQGetListCustomerInRoute" selectionmode="checkbox" height="300" sourceId="partyIdTo"
	customcontrol1="fa fa-map-marker@${uiLabelMap.BSLoadAddress}@javascript: void(0);@CustomerRoute.getAllCustomerNoLatLng()"
	customcontrol2="fa fa-map-marker@${uiLabelMap.BSRemoveCustomer}@javascript: void(0);@CustomerRoute.removeCustomerFromRoute()"/>
