<#assign dataField="[{ name: 'paymentMethodTypeId', type: 'string' },
					 { name: 'accountCode', type: 'string' },
					 { name: 'accountName', type: 'string' },
					 { name: 'accountCodeDef', type: 'string' },
					 { name: 'accountNameDef', type: 'string' }
					 ]
					"/>
<#assign columnlist="{ text: '${uiLabelMap.BACCPaymentMethodType}', filtertype  : 'checkedlist',datafield: 'paymentMethodTypeId',cellsrenderer : function(row){
						var data = $('#jqxgrid').jqxGrid('getrowdata',row);
						if(typeof(data) != 'undefined'){
							for(var i = 0 ;i <listPM.length;i++){
								if(listPM[i].paymentMethodTypeId == data.paymentMethodTypeId){
									return '<span  class=\"custom-style-word\">' + listPM[i].description + '</span>';
								}
							}	
						}
						return '<span></span>';
					},createfilterwidget : function(column,columnElement,widget){
				    	var source = {
				    		localdata : listPM,
				    		datatype : 'array'
				    	};
				    	var filterBoxAdapter = new $.jqx.dataAdapter(source,{autoBind : true});
				    	var uniRecords = filterBoxAdapter.records;
				    	widget.jqxDropDownList({source: uniRecords, displayMember: 'description', valueMember: 'paymentMethodTypeId',placeHolder : '${StringUtil.wrapString(uiLabelMap.BACCPleaseChooseAcc?default(''))}',renderer: function (index, label, value) 
										{
											for(i=0;i < listPM.length; i++){
												if(listPM[i].paymentMethodTypeId == value){
													return listPM[i].description;
												}
											}
										    return value;
										}
				    			});
				    	}
					},
					{ text: '${uiLabelMap.BACCGLAccountId}', datafield: 'accountCode', cellsrenderer:listGlAccountOrganizationAndClassRender,filterable : true},
					{ text: '${uiLabelMap.BACCDefaultGlAccountId}', cellsrenderer:listDefaultGlAccountRender, datafield: 'accountCodeDef', filterable: true}
					 
					"/>
<@jqGrid url="jqxGeneralServicer?organizationPartyId=${parameters.organizationPartyId}&sname=JQListGlAccountNrPaymentMethod" dataField=dataField columnlist=columnlist
		 deletesuccessfunction="OlbGlAccountNrPayment.updateData" functionAfterAddRow="OlbGlAccountNrPayment.updateData"
		 id="jqxgrid" filtersimplemode="true" showtoolbar="true" clearfilteringbutton="true" addrefresh="true"
		 addrow="true" addType="popup" alternativeAddPopup="alterpopupWindow"
		 createUrl="jqxGeneralServicer?organizationPartyId=${parameters.organizationPartyId}&jqaction=C&sname=addPaymentMethodTypeGlAssignment"
		 addColumns="paymentMethodTypeId;glAccountId;organizationPartyId[${parameters.organizationPartyId}]"		 
		 deleterow="true" removeUrl="jqxGeneralServicer?sname=removePaymentMethodTypeGlAssignment&jqaction=D" 
		 deleteColumn="paymentMethodTypeId;organizationPartyId[${parameters.organizationPartyId}]"
 />		