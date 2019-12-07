<#assign dataField="[{ name: 'emplWorkingLateId', type: 'string' },
					 { name: 'partyId', type: 'string' },
					 { name: 'reason', type: 'string' },
					 { name: 'dateWorkingLate', type: 'date', other: 'Timestamp' },
					 { name: 'delayTime', type: 'string'}
					 ]"/>

<#assign columnlist="{ text: '${uiLabelMap.emplWorkingLateId}', datafield: 'emplWorkingLateId', width: 200},
                     { text: '${uiLabelMap.HRReasonLate}', datafield: 'reason', width: 200},
                     { text: '${uiLabelMap.HRDateWorkingLate}', datafield: 'dateWorkingLate', width: 200, cellsformat: 'd', filtertype:'range'},
                     { text: '${uiLabelMap.HRDelayTime} (${uiLabelMap.HRMinute})', datafield: 'delayTime'}
					 "/>

<@jqGrid addrow="false" addType="popup" id="jqxgrid" filtersimplemode="true" showtoolbar="true" clearfilteringbutton="true"
		 url="jqxGeneralServicer?sname=JQGetListEmplWorkLate&partyId=${parameters.partyId}" dataField=dataField columnlist=columnlist
		 />