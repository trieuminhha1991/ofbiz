<#assign dataField="[{ name: 'prepaidExpId', type: 'string' },
					 { name: 'prepaidExpName', type: 'string'},
					 { name: 'acquiredDate', type: 'date', other:'Timestamp'},
					 { name: 'amount', type: 'string'},
					 { name: 'allocPeriodNum', type: 'string'},
					 { name: 'amountEachPeriod', type: 'string'},
					 { name: 'prepaidExpGlAccountId', type: 'string'},
					 { name: 'description', type: 'number'},
				 ]"/>

<#assign columnlist="{ text: '${uiLabelMap.BACCPrepaidExpId}', dataField: 'prepaidExpId', width: 100 },
              		 { text: '${uiLabelMap.BACCPrepaidExpName}', dataField: 'prepaidExpName', width: 200 },
              		 { text: '${uiLabelMap.BACCAcquiredDate}', width: 150, dataField:'acquiredDate', cellsformat: 'dd/MM/yyyy'},
              		 { text: '${uiLabelMap.BACCAmount}', dataField: 'amount', width: 150,
              			cellsrenderer: function(row, columns, value){
	                		  return '<span>'+formatcurrency(value)+'</span>';
              			}
              		 },
              		 { text: '${uiLabelMap.BACCAllocPeriodNum}', dataField: 'allocPeriodNum', width: 150 },
              		 { text: '${uiLabelMap.BACCAmountEachPeriod}', dataField: 'amountEachPeriod', width: 150,
              			cellsrenderer: function(row, columns, value){
	                		  return '<span>'+formatcurrency(value)+'</span>';
            			}
              		 },
              		 { text: '${uiLabelMap.BACCPrepaidExpGlAccountId}', dataField: 'prepaidExpGlAccountId', width: 150 },
              		 { text: '${uiLabelMap.BACCDescription}', dataField: 'description', width: 200 },
					 "/>

<#assign customTime = delegator.findOne("CustomTimePeriod", {"customTimePeriodId" : parameters.customTimePeriodId}, true)>
<#if customTime.isClosed == 'Y'>
	<@jqGrid id="jqxgridPrepaidExp" filtersimplemode="true" editable="false" addType="popup" showtoolbar="true" clearfilteringbutton="true"
		 url="jqxGeneralServicer?sname=JqxGetListAllocPrepaidExp&customTimePeriodId=${parameters.customTimePeriodId}" dataField=dataField columnlist=columnlist
		 />
<#else>
	<@jqGrid id="jqxgridPrepaidExp" filtersimplemode="true" editable="false" addType="popup" customcontrol1="icon-plus open-sans@${uiLabelMap.accAddNewRow}@javascript: void(0);@OLBAvailPE.openWindow()" showtoolbar="true" clearfilteringbutton="true"
		 url="jqxGeneralServicer?sname=JqxGetListAllocPrepaidExp&customTimePeriodId=${parameters.customTimePeriodId}" dataField=dataField columnlist=columnlist
		 />
</#if>             		 