<#assign dataField="[{ name: 'customTimePeriodId', type: 'string' },
					 { name: 'parentPeriodId', type: 'string' },
					 { name: 'periodTypeId', type: 'string' },
					 { name: 'organizationPartyId', type: 'string' },
					 { name: 'fullName', type: 'string' },
					 { name: 'periodNum', type: 'number',other : 'Long'},
					 { name: 'fromDate', type: 'date'},
					 { name: 'thruDate', type: 'date'},
					 { name: 'periodName', type: 'string' }]
					"/>
<#assign columnlist="{ text: '${uiLabelMap.BACCCustomTimePeriodId}', datafield: 'customTimePeriodId', width: 100,  editable:false },
					 { text: '${uiLabelMap.BACCParentPeriodId}', datafield: 'parentPeriodId', width: 300, cellsrenderer:parentPeriodRenderer,columntype : 'dropdownlist',createeditor : function(row,column,editor){
					 	var data = $('#jqxgrid').jqxGrid('getrowdata',row);
					 	editor.jqxDropDownList({displayMember : 'periodName',valueMember : 'customTimePeriodId',source : dataOtp,placeHolder : '${StringUtil.wrapString(uiLabelMap.BACCPleaseChooseAcc?default(''))}'});
					 }},
				 	 { text: '${uiLabelMap.BACCOrganizationParty}', width:300, datafield: 'fullName',editable : false, cellclassname: cellclass,
				 	 },					 
					 { text: '${uiLabelMap.BACCPeriodTypeId}', width:150, datafield: 'periodTypeId', columntype: 'dropdownlist', filtertype: 'checkedlist', 
						cellsrenderer: function (row, column, value) {
							var data = $('#jqxgrid').jqxGrid('getrowdata', row);
        						for(i = 0 ; i < dataPT.length; i++){
        							if(data.periodTypeId == dataPT[i].periodTypeId){
        								return '<span class=\"custom-style-word\" title=' + value +'>' + dataPT[i].description + '</span>';
	        							}
	        						}
	        						return '<span class=\"custom-style-word\" title=' + value +'>' + value + '</span>';
	    						},
	    					createfilterwidget: function (column, columnElement, widget) {
				   				var filterBoxAdapter2 = new $.jqx.dataAdapter(dataPT,
				                {
				                    autoBind: true
				                });
				   				var uniqueRecords2 = filterBoxAdapter2.records;
				   				widget.jqxDropDownList({ source: uniqueRecords2, displayMember: 'periodTypeId', valueMember : 'periodTypeId', renderer: function (index, label, value) 
								{
									for(i=0;i < dataPT.length; i++){
										if(dataPT[i].periodTypeId == value){
											return dataPT[i].description;
										}
									}
								    return value;
								}});
				   				widget.jqxDropDownList('checkAll')
				   			},
				   			createeditor: function (row, column, editor) {
	                            var sourceDPT =
					            {
					                localdata: dataPT,
					                datatype: 'array'
					            };
					            var dataAdapterDPT = new $.jqx.dataAdapter(sourceDPT);
	                            editor.jqxDropDownList({source: dataAdapterDPT, displayMember: 'description', valueMember: 'periodTypeId', autoDropDownHeight: dataPT.length < 8});
	                        },	
	                        initeditor: function (row, cellvalue, editor, celltext, pressedChar) {
						 		if(cellvalue){
							        editor.val(cellvalue);
						 		}
						    }    
					 },
                     { text: '${uiLabelMap.BACCPeriodNumber}', datafield: 'periodNum', width: 140 ,filtertype : 'number'},
                     { text: '${uiLabelMap.BACCStartDate}', datafield: 'fromDate', filtertype: 'range', columntype: 'template', width: 150, cellsformat: 'dd/MM/yyyy', 
                      	createeditor: function (row, column, editor) {
                      	var data = $('#jqxgrid').jqxGrid('getrowdata',row);
                     		editor.jqxDateTimeInput({ width: '150px', height: '25px',  formatString: 'yyyy/MM/dd',value : data.fromDate ?data.fromDate : null });
                     	}},
                     { text: '${uiLabelMap.BACCEndDate}', datafield: 'thruDate', filtertype: 'range', columntype: 'template',cellsformat: 'dd/MM/yyyy',  width: 150,
                      	createeditor: function (row, column, editor) {
                      		var data = $('#jqxgrid').jqxGrid('getrowdata',row);
                     		editor.jqxDateTimeInput({ width: '150px', height: '25px',  formatString: 'yyyy/MM/dd',value : data.fromDate ?data.fromDate : null });
                     	}
                     },
                     { text: '${uiLabelMap.BACCPeriodName}', datafield: 'periodName',width : '17%'}
					 "/>
<@jqGrid url="jqxGeneralServicer?sname=JQListCustomTimePeriod" dataField=dataField columnlist=columnlist editmode="selectedcell" addrefresh="true"
		 addrow="true" updateUrl="jqxGeneralServicer?jqaction=U&sname=updateCustomTimePeriod"   updaterow="true"
		 addType="popup" id="jqxgrid" filtersimplemode="true" showtoolbar="true" editable="true"
		 editColumns="customTimePeriodId;parentPeriodId;periodTypeId;periodName;fromDate(java.sql.Date);periodNum(java.lang.Long);isClosed" createUrl="jqxGeneralServicer?jqaction=C&sname=createCustomTimePeriod"
	     deleterow="true" removeUrl="jqxGeneralServicer?jqaction=D&sname=deleteCustomTimePeriod" deleteColumn="customTimePeriodId"
		 addColumns="periodName;periodNum(java.lang.Long);parentPeriodId;isClosed;periodTypeId;fromDate(java.sql.Date);thruDate(java.sql.Date);organizationPartyId" clearfilteringbutton="true"
		 alternativeAddPopup="alterpopupWindow"
		 jqGridMinimumLibEnable="false"	
		 />