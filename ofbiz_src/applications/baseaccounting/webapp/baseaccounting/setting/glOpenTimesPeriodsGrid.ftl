<#assign dataField="[{ name: 'customTimePeriodId', type: 'string' },
						 { name: 'parentPeriodId', type: 'string' },
						 { name: 'periodTypeId', type: 'string' },
						 { name: 'periodNum', type: 'number' ,other : 'Long'},
						 { name: 'fromDate', type: 'date'},
						 { name: 'thruDate', type: 'date'},
						 { name: 'periodName', type: 'string' },
						 { name: 'isClosed', type: 'string' }]
						"/>
<#assign columnlist="{ text: '${uiLabelMap.BACCCustomTimePeriodId}', datafield: 'customTimePeriodId', width: 150},
					 { text: '${uiLabelMap.BACCParentPeriodId}', datafield: 'parentPeriodId', width: 300, cellsrenderer:parentPeriodRenderer},
					 { text: '${uiLabelMap.BACCPeriodTypeId}', width:150, datafield: 'periodTypeId', columntype: 'dropdownlist', filtertype: 'checkedlist', 
						cellsrenderer: function (row, column, value) {
							var data = $('#jqxgrid').jqxGrid('getrowdata', row);
    						for(i = 0 ; i < dataPT.length; i++){
    							if(data.periodTypeId == dataPT[i].periodTypeId){
    								return '<span title=' + value +'>' + dataPT[i].description + '</span>';
        							}
    						}
    						return '<span title=' + value +'>' + value + '</span>';
						},
    					createfilterwidget: function (column, columnElement, widget) {
    						var uniqueRecords2 = [];
    						if(OlbOpenTimePeriod.accCm.ArrayIsNotEmpty(dataPT)){
    							var filterBoxAdapter2 = new $.jqx.dataAdapter(dataPT,
					                {
					                    autoBind: true
					                });
    							uniqueRecords2 = filterBoxAdapter2.records;
    						}
			   				widget.jqxDropDownList({ source: uniqueRecords2, displayMember: 'description', valueMember : 'periodTypeId'});
			   				widget.jqxDropDownList('checkAll')
			   			}
					 },	    
                     { text: '${uiLabelMap.BACCPeriodNumber}', datafield: 'periodNum', width: 150,filtertype : 'number' },
                     { text: '${uiLabelMap.BACCStartDate}', datafield: 'fromDate', filtertype: 'range', width: 150, cellsformat: 'dd/MM/yyyy'},
                     { text: '${uiLabelMap.BACCEndDate}', datafield: 'thruDate', filtertype: 'range',cellsformat: 'dd/MM/yyyy',  width: 150 },
                     { text: '${uiLabelMap.BACCPeriodName}', datafield: 'periodName'}                     
					 "/>
<@jqGrid url="jqxGeneralServicer?sname=JQListOpenTimePeriod" dataField=dataField columnlist=columnlist
		 addrow="true" updateUrl="jqxGeneralServicer?jqaction=U&sname=updateCustomTimePeriod" addType="popup" id="jqxgrid" filtersimplemode="true" showtoolbar="true"
		 createUrl="jqxGeneralServicer?organizationPartyId=${userLogin.lastOrg}&jqaction=C&sname=createCustomTimePeriod"
		 addColumns="periodName;periodNum(java.lang.Long);parentPeriodId;isClosed;periodTypeId;fromDate(java.sql.Date);thruDate(java.sql.Date);organizationPartyId[${userLogin.lastOrg}]" clearfilteringbutton="true"
		 alternativeAddPopup="alterpopupWindow" defaultSortColumn="customTimePeriodId"
		 deleterow="false" removeUrl="jqxGeneralServicer?sname=closeFinancialTimePeriod&jqaction=D"  
		 deleteColumn="customTimePeriodId" 	mouseRightMenu="true" contextMenuId="contextMenu" customTitleProperties="${uiLabelMap.AccountingOpenTimePeriods}"  
	 />
