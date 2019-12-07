<!--  Begin assign by me event screen -->
<script></script>
<h3>Assign By Me</h3>

<#assign dataField="[{ name: 'workEffortId', type: 'string' },
					 { name: 'description', type: 'string'},
					 { name: 'priority', type: 'string'},
					 { name: 'estimatedStartDate', type: 'date', other:'Timestamp'},
					 { name: 'estimatedCompletionDate', type: 'date', other:'Timestamp'},
					 { name: 'actualStartDate', type: 'date', other:'Timestamp'},
					 { name: 'actualCompletionDate', type: 'date', other:'Timestamp'},
					 { name: 'partyId', type: 'string'},
					 { name: 'complete', type: 'button'},
					 ]"/>

<#assign columnlist="{ text: '${uiLabelMap.workEffortId}', filtertype: 'input', datafield: 'workEffortId', editable: false },
					 { text: '${uiLabelMap.description}', filtertype: 'input', datafield: 'description', editable: false },
					 { text: '${uiLabelMap.priority}', filtertype: 'input', datafield: 'priority', editable: false },
					 { text: '${uiLabelMap.estimatedStartDate}', filtertype: 'range', datafield: 'estimatedStartDate',editable: false, cellsformat: 'dd/MM/yyyy'},
                     { text: '${uiLabelMap.estimatedCompletionDate}', filtertype: 'range', datafield: 'estimatedCompletionDate',editable: false, cellsformat: 'dd/MM/yyyy'},
                     { text: '${uiLabelMap.actualStartDate}', filtertype: 'range', datafield: 'actualStartDate',editable: false,cellsformat: 'dd/MM/yyyy'},
                     { text: '${uiLabelMap.actualCompletionDate}', filtertype: 'range', datafield: 'actualCompletionDate',editable: false, cellsformat: 'dd/MM/yyyy'},
                     { text: '${uiLabelMap.partyId}', filtertype: 'input', datafield: 'partyId',editable: false },
                     { text: '${uiLabelMap.assignToMe}', datafield: 'complete',editable: false }
					 "/>
<@jqGrid id="assignby"  filterable="true" filtersimplemode="true" addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="true" showtoolbar="false" addrow="true" deleterow="true" alternativeAddPopup="alterpopupWindow" editable="true"
		 url="jqxGeneralServicer?sname=getListEventAssignByMe" jqGridMinimumLibEnable="false"/>

<!-- End assign by me event screen -->