<!-- Begin assign to me event screen -->
<script></script>
<h3>Assign To Me</h3>	

<#assign dataField1="[{ name: 'workEffortId', type: 'string' },
					 { name: 'workEffortPurposeTypeId', type: 'string' },
					 { name: 'description', type: 'string'},
					 { name: 'priority', type: 'string'},
					 { name: 'estimatedStartDate', type: 'date', other:'Timestamp'},
					 { name: 'estimatedCompletionDate', type: 'date', other:'Timestamp'},
					 { name: 'actualStartDate', type: 'date', other:'Timestamp'},
					 { name: 'actualCompletionDate', type: 'date', other:'Timestamp'},
					 { name: 'createdByUserLogin', type: 'string'},
					 { name: 'complete', type: 'button'},
					 ]"/>

<#assign columnlist1="{ text: '${uiLabelMap.workEffortId}', filtertype: 'input', datafield: 'workEffortId', editable: false },
					 { text: '${uiLabelMap.workEffortPurposeTypeId}', filtertype: 'input', datafield: 'workEffortPurposeTypeId', editable: false },
					 { text: '${uiLabelMap.description}', filtertype: 'input', datafield: 'description', editable: false },
					 { text: '${uiLabelMap.priority}', filtertype: 'input', datafield: 'priority', editable: false },
					 { text: '${uiLabelMap.estimatedStartDate}', filtertype: 'range', datafield: 'estimatedStartDate',editable: false, cellsformat: 'dd/MM/yyyy'},
                     { text: '${uiLabelMap.estimatedCompletionDate}', filtertype: 'range', datafield: 'estimatedCompletionDate',editable: false, cellsformat: 'dd/MM/yyyy'},
                     { text: '${uiLabelMap.actualStartDate}', filtertype: 'range', datafield: 'actualStartDate',editable: false, cellsformat: 'dd/MM/yyyy'},
                     { text: '${uiLabelMap.actualCompletionDate}', filtertype: 'range', datafield: 'actualCompletionDate',editable: false, cellsformat: 'dd/MM/yyyy'},
                     { text: '${uiLabelMap.createdByUserLogin}', filtertype: 'input', datafield: 'createdByUserLogin',editable: false },
                     { text: '${uiLabelMap.assignToMe}', datafield: 'complete',editable: false }
					 "/>
<@jqGrid id="assignto" filterable="true" filtersimplemode="true" addType="popup" dataField=dataField1 columnlist=columnlist1 clearfilteringbutton="true" showtoolbar="false" addrow="true" deleterow="true" alternativeAddPopup="alterpopupWindow" editable="true"
		 url="jqxGeneralServicer?sname=getListEventAssignToMe" />
<!-- Begin assign by me event screen -->