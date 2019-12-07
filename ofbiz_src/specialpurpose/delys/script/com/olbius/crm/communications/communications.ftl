<script></script>
<#assign dataField="[{ name: 'partyId', type: 'string' },
					 { name: 'infoString', type: 'string'},
					 { name: 'tnContactNumber', type: 'string'},
					 { name: 'paCity', type: 'string'},
					 { name: 'paCountryGeoId', type: 'string'},
					 { name: 'groupName', type: 'string'},
					 { name: 'assign', type: 'button'},
					 ]"/>

<#assign columnlist="{ text: '${uiLabelMap.partyID}', filtertype: 'input', datafield: 'partyId', editable: false,
					 },
					 { text: '${uiLabelMap.emailAddr}', filtertype: 'input', datafield: 'infoString', editable: false},
					 { text: '${uiLabelMap.phoneNumber}', datafield: 'tnContactNumber', columntype: 'template', 
					 },
					 { text: '${uiLabelMap.city}', filtertype: 'input', datafield: 'paCity',editable: false,
                     },
                     { text: '${uiLabelMap.country}', filtertype: 'input', datafield: 'paCountryGeoId',editable: false,
                     },
                     { text: '${uiLabelMap.relatedCompany}', filtertype: 'input', datafield: 'groupName',editable: false,
                     },
                     { text: '${uiLabelMap.assignToMe}', datafield: 'assign',editable: false,
                     }
					 "/>
<@jqGrid filterable="true" filtersimplemode="true" addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="true" showtoolbar="false" addrow="true" deleterow="true" alternativeAddPopup="alterpopupWindow" editable="true"
		 url="jqxGeneralServicer?sname=getListAccounts" />