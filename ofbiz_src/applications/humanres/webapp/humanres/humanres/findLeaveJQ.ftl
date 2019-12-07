
 <#assign columnlist="{ text: 'Gl Account Id', dataField: 'glAccountId', pinned: true, width: 300 },
                     { text: 'Parent Gl Account Id', dataField: 'parentGlAccountId', width: 300,
                     	aggregates: ['count',
                          { '121000 Items':
                            function (aggregatedValue, currentValue) {
                                if (currentValue == \"121000\") {
                                    return aggregatedValue + 1;
                                }
                                return aggregatedValue;
                            }
                          }
                      ]
                     },
                     { text: 'Account Code', dataField: 'accountCode', width: 300 },
                     { text: 'Account Name', dataField: 'accountName', width: 300 },
                     { text: 'Description', dataField: 'description', width: 300 },
                     { text: 'Posted Balance', dataField: 'postedBalance', width: 300 } "/>
<#assign dataField="{ name: 'glAccountId', type: 'string' },
                 { name: 'parentGlAccountId', type: 'string' },
                 { name: 'accountCode', type: 'string' },
                 { name: 'accountName', type: 'string' },
                 { name: 'description', type: 'string' },
                 { name: 'postedBalance', type: 'string' } "/>
<#--                 
 <@jqTable pageable="true" url="/humanres/control/FindEmplLeaves2" columnlist=columnlist dataField=dataField entityName="GlAccount"/>
-->
<#--
<#assign columnlist="{ text: 'Party Id', dataField: 'partyId', pinned: true, width: 300 },
                  		 { text: 'Leave type Id', dataField: 'leaveTypeId', width: 300 },
                  		 { text: 'Leave status', dataField: 'leaveStatus', width: 300 },
                  		 { text: 'Leave reason type', dataField: 'emplLeaveReasonTypeId', width: 300 }"/>
<#assign dataField="{ name: 'partyId', type: 'string' },
               		{ name: 'leaveTypeId', type: 'string' },
                	{ name: 'leaveStatus', type: 'string' },
                	{ name: 'emplLeaveReasonTypeId', type: 'string' }"/>
-->                	
<@jqGrid url="/humanres/control/FindEmplLeaves2" defaultSortColumn="glAccountId" columnlist=columnlist dataField=dataField height="400" 
		 entityName="GlAccount" updateUrl="/humanres/control/FindEmplLeaves3" filtersimplemode="false"/>                	
