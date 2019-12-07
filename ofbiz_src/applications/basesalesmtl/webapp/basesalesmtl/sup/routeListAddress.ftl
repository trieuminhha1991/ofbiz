<#assign dataField="[{ name: 'contactMechId', type: 'string' },
		            { name: 'address1', type: 'string' },
		            { name: 'countryGeoId', type: 'string' },
		            { name: 'stateProvinceGeoId', type: 'string' },
		            { name: 'districtGeoId', type: 'string' },
		            { name: 'wardGeoId', type: 'string' },
		            { name: 'countryGeo', type: 'string' },
		            { name: 'stateProvinceGeo', type: 'string' },
		            { name: 'districtGeo', type: 'string' },
		            { name: 'wardGeo', type: 'string' },
		            { name: 'note', type: 'string' },
		            { name: 'latitude', type: 'number' },
		            { name: 'longitude', type: 'number' },
		            { name: 'postalCode', type: 'string'}]"/>
<#assign columnlist="{ text: '${uiLabelMap.DmsAddress1}', datafield: 'address1', minwidth: 250},
					{ text: '${uiLabelMap.DmsCounty}', datafield: 'districtGeo', width: 200},
					{ text: '${uiLabelMap.DmsProvince}', datafield: 'stateProvinceGeo', width: 200},"/>

<@jqGrid id="RouteListAddress" dataField=dataField columnlist=columnlist isShowTitleProperty="false"
	showtoolbar="true" customLoadFunction="true" jqGridMinimumLibEnable="false" height="300" autoheight="false" filterable="false" refreshbutton="true"
	customcontrol1="fa fa-trash@${uiLabelMap.BSDeleteAddress}@javascript: void(0);@RouteAddress.deleteAddress()"
	customcontrol2="fa fa-users@${uiLabelMap.BSAddCustomer}@javascript: void(0);@RouteAddress.addCustomer()"
	url=""/>
