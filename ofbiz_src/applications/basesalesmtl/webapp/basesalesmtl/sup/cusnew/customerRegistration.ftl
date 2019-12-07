<!-- TODO deleted change to basesalesmtl/webapp/basesalesmtl/customer/request/requestNewCustomerList.ftl -->
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>
<style>
.update {
	background-color: #d9edf7 !important;
}
.update.jqx-widget-olbius .update.jqx-grid-cell-selected-olbius, .jqx-widget-olbius .update.jqx-grid-cell-hover-olbius {
	background-color: #d9edf7 !important;
}
.create {
	background-color: #dff0d8 !important;
}
.create.jqx-widget-olbius .create.jqx-grid-cell-selected-olbius, .jqx-widget-olbius .create.jqx-grid-cell-hover-olbius {
	background-color: #dff0d8 !important;
}
</style>
<script>
	var states = [<#list partyStatus?if_exists as status>
		{
			statusId : "${status.statusId}",
			description: "${StringUtil.wrapString(status.get('description',locale))}"
		},
	</#list>]
	var cellclassname = function (row, column, value, data) {
		var statusId = data.statusId;
		if (statusId == "PARTY_UPDATED") {
			return "update";
		}
        return "create";
    };
</script>
<#assign id="CustomerRegistration"/>
<#assign dataField="[{ name: 'customerId', type: 'string'},
					{ name: 'customerName', type: 'string'},
					{ name: 'officeSiteName', type: 'string'},
					{ name: 'routeId', type: 'string'},
					{ name: 'routeName', type: 'string'},
					{ name: 'address', type: 'string'},
					{ name: 'stateProvinceGeoId', type: 'string'},
					{ name: 'stateProvinceGeoName', type: 'string'},
					{ name: 'stateProvinceGeoNameG', type: 'string'},
					{ name: 'districtGeoId', type: 'string'},
					{ name: 'districtGeoName', type: 'string'},
					{ name: 'districtGeoNameG', type: 'string'},
					{ name: 'latitude', type: 'string'},
					{ name: 'longitude', type: 'string'},
					{ name: 'url', type: 'string'},
					{ name: 'createdByUserLogin', type: 'string'},
					{ name: 'lastUpdatedByUserLogin', type: 'string'},
					{ name: 'note', type: 'string'},
					{ name: 'statusId', type: 'string'},
					{ name: 'gender', type: 'string'},
					{ name: 'productStoreId', type: 'string'},
					{ name: 'storeName', type: 'string'},
					{ name: 'phone', type: 'string'},
					{ name : 'salesmanId', type: 'string'},
					{ name: 'salesmanName', type: 'string'},
					{ name: 'startDate', type: 'date', other: 'Timestamp'},
					{ name: 'birthDate', type: 'date'}]"/>
<#assign columnlist="{ text: '${StringUtil.wrapString(uiLabelMap.DAOwnerStoreName)}', datafield: 'customerName', width: 150, cellclassname: cellclassname},
					{ text: '${StringUtil.wrapString(uiLabelMap.BSAgentName)}', datafield: 'officeSiteName', width: 150, cellclassname: cellclassname},
					{ text: '${StringUtil.wrapString(uiLabelMap.BSRouteName)}', datafield: 'routeName', width: 150, cellclassname: cellclassname},
					{ text: '${StringUtil.wrapString(uiLabelMap.PhoneNumber)}', datafield: 'phone', width: 150, cellclassname: cellclassname},
					{ text: '${StringUtil.wrapString(uiLabelMap.BSAddress)}', datafield: 'address', width: 150, cellclassname: cellclassname},
					{ text: '${StringUtil.wrapString(uiLabelMap.District)}', datafield: 'districtGeoNameG', width: 150, cellclassname: cellclassname,
					    cellsrenderer: function(row, column, value){
					        if (OlbCore.isNotEmpty(value)) {
					            return '<span>' + value + '</span>';
					        }
                            var data = $('#CustomerRegistration').jqxGrid('getrowdata', row);
                            if (OlbCore.isNotEmpty(data.districtGeoName)) {
					            return '<span>' + data.districtGeoName + '</span>';
					        }
                            return \"\";
						}
					},
					{ text: '${StringUtil.wrapString(uiLabelMap.CityProvince)}', datafield: 'stateProvinceGeoNameG', width: 150, cellclassname: cellclassname,
					    cellsrenderer: function(row, column, value){
					        if (OlbCore.isNotEmpty(value)) {
					            return '<span>' + value + '</span>';
					        }
					        var data = $('#CustomerRegistration').jqxGrid('getrowdata', row);
					        if (OlbCore.isNotEmpty(data.stateProvinceGeoName)) {
					            return '<span>' + data.stateProvinceGeoName + '</span>';
					        }
                            return \"\";
						}
					},
					{ text: '${StringUtil.wrapString(uiLabelMap.BSLatitude)}', datafield: 'latitude', width: 150, cellclassname: cellclassname},
					{ text: '${StringUtil.wrapString(uiLabelMap.BSLongitude)}', datafield: 'longitude', width: 150, cellclassname: cellclassname},
					{ text: '${StringUtil.wrapString(uiLabelMap.BSLinkImage)}', datafield: 'url', width: 150, cellclassname: cellclassname,
						 cellsrenderer:  function (row, column, value, a, b, data){
						   var vl = '\"' + value + '\"';
						   var str = \"<div class='cell-custom-grid'><a href='javascript:CustomerRegistration.viewImage(\"
									+ vl +\")' \";
						   if(!value){
								str += ' class=\"disabled\"' ;
						   }
						   str += \">${StringUtil.wrapString(uiLabelMap.BSViewImage)}</a></div>\"
						   return str;
					   }
					},
					{ text: '${StringUtil.wrapString(uiLabelMap.DmsPartyGender)}', datafield: 'gender', width: 150, cellclassname: cellclassname},
					{ text: '${StringUtil.wrapString(uiLabelMap.BSStatus)}', datafield: 'statusId', width: 150, filtertype: 'checkedlist', cellclassname: cellclassname,
						cellsrenderer: function(row, column, value){
							for(var i = 0; i < states.length; i++){
								if(value == states[i].statusId){
									return '<div class=\"cell-custom-grid\">' + states[i].description + '</div>';
								}
							}
							return '<span>' + value + '</span>';
						},
						createfilterwidget: function (column, columnElement, widget) {
							widget.jqxDropDownList({ filterable:true,source: states, displayMember: 'description', valueMember : 'statusId'});
						}
					},
					{ text: '${StringUtil.wrapString(uiLabelMap.BSCreatedBy)}', datafield: 'createdByUserLogin', width: 150, cellclassname: cellclassname},
					{ text: '${StringUtil.wrapString(uiLabelMap.BSUpdatedBy)}', datafield: 'lastUpdatedByUserLogin', width: 150, cellclassname: cellclassname},
					{ text: '${StringUtil.wrapString(uiLabelMap.DADistributor)}', datafield: 'productStoreId', width: 150, cellclassname: cellclassname},
					{ text: '${StringUtil.wrapString(uiLabelMap.BSNote)}', datafield: 'note', width: 150, cellclassname: cellclassname},
					"/>

<@jqGrid id=id dataField=dataField columnlist=columnlist clearfilteringbutton="true" selectionmode="singlerow"
	showtoolbar="true" url="jqxGeneralServicer?sname=JQGetCustomerRegistration" contextMenuId="Context${id}"  mouseRightMenu="true"
	updateUrl="jqxGeneralServicer?jqaction=U&sname=updateRequestNewCustomer" editColumns="customerId;customerName;officeSiteName;phone;address;stateProvinceGeoId;districtGeoId;gender;note;url;productStoreId;salesmanId;routeId"
/>


<#include "customerRegAction.ftl"/>
<#include "customerRegContextMenu.ftl"/>