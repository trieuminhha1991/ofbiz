<#assign dataField="[{ name: 'paymentTypeId', type: 'string' },
					 { name: 'glAccountTypeId', type: 'string' }
					 ]
					"/>
<#assign columnlist="{ text: '${uiLabelMap.AccountingPaymentType}', datafield: 'paymentTypeId',filtertype : 'checkedlist',cellsrenderer : function(row){
							var data = $('#jqxgrid').jqxGrid('getrowdata',row);
							if(typeof(data) == 'undefined') return '';
							for(var i = 0 ;i <listPM.length;i++){
								if(listPM[i].paymentTypeId == data.paymentTypeId){
									return '<span>' + listPM[i].description + '</span>';
								}
							}
							return '<span>' + data.paymentTypeId+ '</span>';
						},createfilterwidget : function(column,columnElement,widget){
					    	var source = {
					    		localdata : listPM,
					    		datatype : 'array'
					    	};
					    	var filterBoxAdapter = new $.jqx.dataAdapter(source,{autoBind : true});
					    	var uniRecords = filterBoxAdapter.records;
					    	widget.jqxDropDownList({source: uniRecords,filterable : true,searchMode : 'containsignorecase', displayMember: 'description', valueMember: 'paymentTypeId'});
					    }
					},
					{ text: '${uiLabelMap.BACCGlAccountType}', datafield: 'glAccountTypeId',filtertype : 'checkedlist',cellsrenderer : function(row){
						var data = $('#jqxgrid').jqxGrid('getrowdata',row);
						if(typeof(data) == 'undefined') return '';
						for(var index in dataGLAT){
							if(dataGLAT[index].glAccountTypeId == data.glAccountTypeId){
								return '<span>' + dataGLAT[index].description  + '</span>';
							}
						}
						return data.glAccountTypeId;
						},createfilterwidget : function(row,cellvalue,widget){
							var source  = {
								localdata : dataGLAT,
								datatype : 'array',
							};
							var filterBoxAdapter = new $.jqx.dataAdapter(source,{autoBind : true});
					    	var uniRecords = filterBoxAdapter.records;
							widget.jqxDropDownList({source: uniRecords, displayMember: 'description',filterable : true,searchMode : 'containsignorecase', valueMember: 'glAccountTypeId',placeHolder : '${StringUtil.wrapString(uiLabelMap.BACCPleaseChooseAcc?default(''))}'								});
						}
					}
					"/>
<@jqGrid url="jqxGeneralServicer?organizationPartyId=${userLogin.lastOrg}&sname=JQListGlAccountTypePaymentType" dataField=dataField columnlist=columnlist 
		 id="jqxgrid" filtersimplemode="true" showtoolbar="true" clearfilteringbutton="true" 
		 addrow="true" addType="popup" alternativeAddPopup="alterpopupWindow"
		 createUrl="jqxGeneralServicer?organizationPartyId=${userLogin.lastOrg}&jqaction=C&sname=addPaymentTypeGlAssignment"
		 addColumns="paymentTypeId;glAccountTypeId;organizationPartyId[${userLogin.lastOrg}]"
		deletesuccessfunction="OlbGlAccountTypePayment.updateData" functionAfterAddRow="OlbGlAccountTypePayment.updateData"
		 deleterow="true" removeUrl="jqxGeneralServicer?sname=removePaymentTypeGlAssignment&jqaction=D" 
		 deleteColumn="paymentTypeId;organizationPartyId[${userLogin.lastOrg}]"
 />		