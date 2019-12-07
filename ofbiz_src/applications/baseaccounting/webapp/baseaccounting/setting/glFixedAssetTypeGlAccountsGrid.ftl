<#assign dataField="[{ name: 'fixedAssetTypeId', type: 'string'},
					 { name: 'fixedAssetId', type: 'string'},
					 { name: 'assetGlAccountId', type: 'string'},
					 { name: 'accDepGlAccountId', type: 'string'},
					 { name: 'depGlAccountId', type: 'string'},
					 { name: 'profitGlAccountId', type: 'string'},
					 { name: 'lossGlAccountId', type: 'string'},
					]"/>

<#assign columnlist="{ text: '${uiLabelMap.BACCFixedAssetTypeId}', datafield: 'fixedAssetTypeId',cellsrenderer:assetTypeRenderer,width : '20%',filtertype : 'checkedlist',createfilterwidget : function(column,columnElement,widget){
						var uniRecords = []
						if(dataLFLAT && dataLFLAT.length > 0){
							var source = {
					    		localdata : dataLFLAT,
					    		datatype : 'array'
					    	};
					    	var filterBoxAdapter = new $.jqx.dataAdapter(source,{autoBind : true});
					    	uniRecords = filterBoxAdapter.records;
						}
				    	widget.jqxDropDownList({filterable : true,source: uniRecords, displayMember: 'description', valueMember: 'fixedAssetTypeId',placeHolder : '${StringUtil.wrapString(uiLabelMap.BACCPleaseChooseAcc?default(''))}' });
				    	widget.jqxDropDownList('checkAll');
				    	}
				    },
					{ text: '${uiLabelMap.BACCFixedAssetId}', datafield: 'fixedAssetId',cellsrenderer:fixedAssetRenderer,width : '20%',filtertype : 'checkedlist',createfilterwidget : function(column,columnElement,widget){
							var uniRecords  = [];
							if(dataLFA && dataLFA.length > 0){
						    	var source = {
						    		localdata : dataLFA,
						    		datatype : 'array'
						    	};
						    	var filterBoxAdapter = new $.jqx.dataAdapter(source,{autoBind : true});
						    	uniRecords = filterBoxAdapter.records;
						    }	
					    	widget.jqxDropDownList({source: uniRecords, displayMember: 'description', valueMember: 'fixedAssetId',placeHolder : '${StringUtil.wrapString(uiLabelMap.BACCPleaseChooseAcc?default(''))}'});
					    	widget.jqxDropDownList('checkAll');
						}
				    },
					{ text: '${uiLabelMap.BACCAssetGlAccountId}', datafield: 'assetGlAccountId',width : '20%', cellsrenderer:listAssetGlAccountRender,filtertype : 'checkedlist',createfilterwidget : function(column,columnElement,widget){
						var uniRecords = []
							if(dataLAGLA && dataLAGLA.length > 0){	
								var source = {
							    		localdata : dataLAGLA,
							    		datatype : 'array'
							    	};
						    	var filterBoxAdapter = new $.jqx.dataAdapter(source,{autoBind : true});
						    	var uniRecords = filterBoxAdapter.records;
							}
					    	widget.jqxDropDownList({source: uniRecords,dropDownHeight : 200, displayMember: 'description', valueMember: 'glAccountId',placeHolder : '${StringUtil.wrapString(uiLabelMap.BACCPleaseChooseAcc?default(''))}'});
					    	widget.jqxDropDownList('checkAll');
						}
				    },
					{ text: '${uiLabelMap.BACCDepGlAccountId}', datafield: 'accDepGlAccountId',width : '20%', cellsrenderer:listAccDeptGlAccountRender,filtertype : 'checkedlist',createfilterwidget : function(column,columnElement,widget){
						var uniRecords = [];
						if(dataLADGLA && dataLADGLA.length > 0){
							var source = {
						    		localdata : dataLADGLA,
						    		datatype : 'array'
						    	};
					    	var filterBoxAdapter = new $.jqx.dataAdapter(source,{autoBind : true});
					    	uniRecords = filterBoxAdapter.records;
						}
				    	widget.jqxDropDownList({source: uniRecords, displayMember: 'description', valueMember: 'glAccountId',placeHolder : '${StringUtil.wrapString(uiLabelMap.BACCPleaseChooseAcc?default(''))}'});
				    	widget.jqxDropDownList('checkAll');
						}
				    },
					{ text: '${uiLabelMap.BACCdepGlAccountId}', datafield: 'depGlAccountId',width : '20%', cellsrenderer:listDeptGlAccountRender,filtertype : 'checkedlist',createfilterwidget : function(column,columnElement,widget){
						var uniRecords = [];
						if(dataLDGLA && dataLDGLA.length > 0){
							var source = {
						    		localdata : dataLDGLA,
						    		datatype : 'array'
						    	};
						    	var filterBoxAdapter = new $.jqx.dataAdapter(source,{autoBind : true});
						    	uniRecords = filterBoxAdapter.records;
						}
					    	widget.jqxDropDownList({source: uniRecords, displayMember: 'description', valueMember: 'glAccountId',placeHolder : '${StringUtil.wrapString(uiLabelMap.BACCPleaseChooseAcc?default(''))}'
					    	});
					    	widget.jqxDropDownList('checkAll');
					    }
				    },
					{ text: '${uiLabelMap.BACCProfitGlAccountId}', datafield: 'profitGlAccountId',width : '20%', cellsrenderer:listProfitGlAccountRender,filtertype : 'checkedlist',createfilterwidget : function(column,columnElement,widget){
						var uniRecords = [];
						if(dataLPGLA && dataLPGLA.length > 0){	
							var source = {
						    		localdata : dataLPGLA,
						    		datatype : 'array'
						    	};
						    	var filterBoxAdapter = new $.jqx.dataAdapter(source,{autoBind : true});
						    	uniRecords = filterBoxAdapter.records;
						}
				    	widget.jqxDropDownList({source: uniRecords, displayMember: 'description', valueMember: 'glAccountId',placeHolder : '${StringUtil.wrapString(uiLabelMap.BACCPleaseChooseAcc?default(''))}'
				    	});
				    	widget.jqxDropDownList('checkAll');
					    }
				    },
					{ text: '${uiLabelMap.BACCLossGlAccountId}', datafield: 'lossGlAccountId',width : '20%', cellsrenderer:listLossGlAccountRender,filtertype : 'checkedlist',createfilterwidget : function(column,columnElement,widget){
						var uniRecords  = []
						if(dataLLGLA &&dataLLGLA.length > 0  )	{
					    	var source = {
						    		localdata : dataLLGLA,
						    		datatype : 'array'
						    	};
					    	var filterBoxAdapter = new $.jqx.dataAdapter(source,{autoBind : true});
					    	uniRecords = filterBoxAdapter.records;
					    }
						
					    	widget.jqxDropDownList({source: uniRecords, displayMember: 'description', valueMember: 'glAccountId',placeHolder : '${StringUtil.wrapString(uiLabelMap.BACCPleaseChooseAcc?default(''))}'
					    	});
					    	widget.jqxDropDownList('checkAll');
					    }
				    }
					"/>
	
<@jqGrid  filtersimplemode="true" addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="true" showtoolbar="true"
		 addrow="true" deleterow="true" alternativeAddPopup="alterpopupWindow"
		 url="jqxGeneralServicer?sname=JQListFixedAssetTypeyGLAccounts&organizationPartyId=${userLogin.lastOrg}"
		 removeUrl="jqxGeneralServicer?sname=deleteFixedAssetTypeGlAccount&jqaction=D&organizationPartyId=${userLogin.lastOrg}"
		 createUrl="jqxGeneralServicer?sname=createFixedAssetTypeGlAccount&jqaction=C&organizationPartyId=${userLogin.lastOrg}"
		 deleteColumn="fixedAssetTypeId;fixedAssetId;organizationPartyId[${userLogin.lastOrg}]"
		 addColumns="fixedAssetTypeId;fixedAssetId;assetGlAccountId;accDepGlAccountId;depGlAccountId;profitGlAccountId;lossGlAccountId;organizationPartyId[${userLogin.lastOrg}]" 
	 />
