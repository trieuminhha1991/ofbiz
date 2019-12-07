<#assign dataField="[{ name: 'customTimePeriodId', type: 'string' },
						 { name: 'parentPeriodId', type: 'string' },
						 { name: 'periodTypeId', type: 'string' },
						 { name: 'periodNum', type: 'number' ,other : 'Long'},
						 { name: 'fromDate', type: 'date'},
						 { name: 'thruDate', type: 'date'},
						 { name: 'periodName', type: 'string' }
						 ]
						"/>
<#assign columnlist="{ text: '${uiLabelMap.BACCCustomTimePeriodId}', datafield: 'customTimePeriodId', width: 150},
					 { text: '${uiLabelMap.BACCParentPeriodId}', datafield: 'parentPeriodId', width: 300, cellsrenderer:parentPeriodRenderer},
					 { text: '${uiLabelMap.BACCPeriodTypeId}', width:200, datafield: 'periodTypeId', columntype: 'dropdownlist', filtertype: 'checkedlist', 
						cellsrenderer: function (row, column, value) {
							var data = $('#jqxGridClosed').jqxGrid('getrowdata', row);
    						for(i = 0 ; i < dataPT.length; i++){
    							if(data.periodTypeId == dataPT[i].periodTypeId){
    								return '<span title=' + value +'>' + dataPT[i].description + '</span>';
    							}
    						}
    						return '<span title=' + value +'>' + value + '</span>';
						},
    					createfilterwidget: function (column, columnElement, widget) {
    						var uniqueRecords2 = [];
    						if(dataPT.length == 0){
    							var filterBoxAdapter2 = new $.jqx.dataAdapter(dataPT, {
					                    autoBind: true
					                }
    							);
    							uniqueRecords2 = filterBoxAdapter2.records;
    						}
			   				widget.jqxDropDownList({ source: uniqueRecords2, displayMember: 'description', valueMember : 'periodTypeId'});
			   				widget.jqxDropDownList('checkAll')
			   			}
					 },	
                     { text: '${uiLabelMap.BACCPeriodNumber}', datafield: 'periodNum', width: 150,filtertype : 'number' },
                     { text: '${uiLabelMap.BACCStartDate}', datafield: 'fromDate', filtertype: 'range', width: 150, cellsformat: 'dd/MM/yyyy'},
                     { text: '${uiLabelMap.BACCEndDate}', datafield: 'thruDate', filtertype: 'range',cellsformat: 'dd/MM/yyyy',  width: 150 },
                     { text: '${uiLabelMap.BACCPeriodName}', datafield: 'periodName',  width: 250}	                 
					 "/>
<@jqGrid url="jqxGeneralServicer?sname=JQListClosedTimePeriod" dataField=dataField columnlist=columnlist
		 addrow="false"  addType="popup" filtersimplemode="true" showtoolbar="true"
		 createUrl="jqxGeneralServicer?organizationPartyId=${userLogin.lastOrg}&jqaction=C&sname=createCustomTimePeriod" customTitleProperties="${uiLabelMap.AccountingClosedTimePeriods}"
		 addColumns="periodName;periodNum(java.lang.Long);parentPeriodId;isClosed;periodTypeId;fromDate(java.sql.Date);thruDate(java.sql.Date);organizationPartyId[${userLogin.lastOrg}]" clearfilteringbutton="true"		 
		 id="jqxGridClosed"
		 />