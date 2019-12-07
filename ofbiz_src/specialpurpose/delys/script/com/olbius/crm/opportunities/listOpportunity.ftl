<script></script>
<#assign dataField="[{ name: 'opportunityName', type: 'string' },
					 { name: 'stagedescription', type: 'string'},
					 { name: 'partyId', type: 'string'},
					 { name: 'estimatedAmount', type: 'string'},
					 { name: 'nextStep', type: 'string'},
					 { name: 'nextStepDate', type: 'date'},
					 { name: 'estimatedCloseDate', type: 'date'},
					 { name: 'close', type: 'button'},
					 ]"/>

<#assign columnlist="{ text: '${uiLabelMap.opportunityName}', filtertype: 'input', datafield: 'opportunityName', editable: false,
					 },
					 { text: '${uiLabelMap.initialStage}', filtertype: 'input', datafield: 'stagedescription', editable: false},
					 { text: '${uiLabelMap.partyId}', filtertype: 'input', datafield: 'partyId', editable: false
					 },
					 { text: '${uiLabelMap.estimateAmount}', filtertype: 'number', datafield: 'estimatedAmount', editable: false,
                     },
                     { text: '${uiLabelMap.nextStep}', filtertype: 'input', datafield: 'nextStep', editable: false,
                     },
                     { text: '${uiLabelMap.nextStepDate}', filtertype: 'date', datafield: 'nextStepDate',editable: false,
                     },
                     { text: '${uiLabelMap.closeDate}', filtertype: 'date', datafield: 'estimatedCloseDate', editable: false,
                     },{ text: '${uiLabelMap.close}', datafield: 'close', editable: false,
                     }
					 "/>
<@jqGrid filterable="true" filtersimplemode="true" addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="true" showtoolbar="false" addrow="true" deleterow="true" alternativeAddPopup="alterpopupWindow" editable="true"
		 url="jqxGeneralServicer?sname=getListOpportunities" />