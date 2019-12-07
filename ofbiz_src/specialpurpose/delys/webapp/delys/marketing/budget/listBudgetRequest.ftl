<div id="person">
	<#assign dataField="[{ name: 'partyId', type: 'string' },
				 { name: 'firstName', type: 'string'},
				 { name: 'middleName', type: 'string'},
				 { name: 'lastName', type: 'string'},
				 { name: 'birthDate', type: 'string'}]"/>

	<#assign columnlist="{ text: '${uiLabelMap.partyID}', filtertype: 'input', datafield: 'partyId', editable: false },
					   	 { text: '${uiLabelMap.firstName}', filtertype: 'input', datafield: 'firstName',editable: false},
	                     { text: '${uiLabelMap.middleName}', filtertype: 'input', datafield: 'middleName',editable: false, hidden: true },
	                     { text: '${uiLabelMap.lastName}', filtertype: 'input', datafield: 'lastName',editable: false, hidden: true },
	                     { text: '${uiLabelMap.birthday}', filtertype: 'input', datafield: 'birthDate',editable: false, hidden: true }"/>
	                      

</div>
	
