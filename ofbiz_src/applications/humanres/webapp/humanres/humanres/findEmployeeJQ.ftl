 
<#assign columnlist="{ text: 'Party Id', dataField: 'PTPartyId', filtertype: 'date', pinned: true, width: 200,
						aggregates: ['count'], cellsrenderer: linkrenderer
					 },
					 { text: 'First name', dataField: 'PSFirstName', filtertype: 'date', width: 150 },
					 { text: 'Middle name', dataField: 'PSMiddleName', width: 150 },
					 { text: 'Last name', dataField: 'PSLastName', width: 150 },
					 { text: 'Gender', dataField: 'PSGender', width: 150 },					 
					 { text: 'Birth date', dataField: 'PSBirthDate', filtertype: 'date', width: 150 },
					 { text: 'Social security number', dataField: 'PSSocialSecurityNumber', width: 150 },
					 { text: 'Salutation', dataField: 'PSSalutation', width: 150 },
					 { text: 'Marital status', dataField: 'PSMaritalStatus', width: 150 }
					 "/>
<#assign dataField="{ name: 'PTPartyId', type: 'string'},
					{ name: 'PSFirstName', type: 'string' },
					{ name: 'PSLastName', type: 'string' },
					{ name: 'PSGender', type: 'string' },
					{ name: 'PSBirthDate', type: 'date' },
					{ name: 'PSSocialSecurityNumber', type: 'string' },
					{ name: 'PSSalutation', type: 'string' },
					{ name: 'PSMaritalStatus', type: 'string' }"/>

<@jqGrid entityName="FullEmployeeInformation" url="/humanres/control/FindEmployeeJQ" defaultSortColumn="partyId" columnlist=columnlist dataField=dataField height="400" 
		 editable="true" filtersimplemode="true" initrowdetails="true" editpopup="true"/>              