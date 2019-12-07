<#assign dataField="[{ name: 'finAccountTypeId', type: 'string' },
               		{ name: 'organizationPartyId', type: 'string' },
               		{ name: 'glAccountId', type: 'string' }
               		]"/>
               		
<#assign columnlist="{ text: '${uiLabelMap.AccountingFinAccountTypeGlAccount}', datafield: 'finAccountTypeId',filtertype : 'checkedlist',editable : false,cellsrenderer : function(row){
						var data = $('#jqxgrid').jqxGrid('getrowdata',row);
						for(var i = 0 ;i < dataFLAT.length; i++){
							if(dataFLAT[i].finAccountTypeId == data.finAccountTypeId){
								return '<span>' + dataFLAT[i].description + '</span>';
							}	
						}
						return data.finAccountTypeId;
					},createfilterwidget : function(column,columnElement,widget){
						var uniRecords = [];
						if(dataFLAT && dataFLAT.length > 0){
							var source = {
						    		localdata : dataFLAT,
						    		datatype : 'array'
						    	};
						    	var filterBoxAdapter = new $.jqx.dataAdapter(source,{autoBind : true});
						    	uniRecords = filterBoxAdapter.records;
						}
					    	widget.jqxDropDownList({source: uniRecords, displayMember: 'description', valueMember: 'finAccountTypeId',placeHolder : '${StringUtil.wrapString(uiLabelMap.BACCPleaseChooseAcc?default(''))}'});
					    	widget.jqxDropDownList('checkAll');
						}
					},					 
			    	{ text: '${uiLabelMap.BACCAccountCode}', dataField: 'glAccountId', columntype: 'template', width: '600',
				         	cellsrenderer: function(row, columns, value){
				         		var data = $('#jqxgrid').jqxGrid('getrowdata',row);
								for(i=0;i < dataGLOAC.length; i++){
									if(dataGLOAC[i].glAccountId == value){
										return '<span>' + dataGLOAC[i].description + '</span>';
									}
								}
				         		return '';
				         	},createeditor : function(row, cellvalue, editor, celltext, cellwidth, cellheight){
				         		editor.append('<div id=\"jqxgridEditGlAccount\"></div>');
				         		accCm.initDropDownGlAccountOrg('getListGLAccountOACsData',editor,$('#jqxgridEditGlAccount'),{wgrid : 600,wbt : 600});
				         		editor.jqxDropDownButton('setContent',cellvalue);
				         	},geteditorvalue : function(row,cellvalue,editor){
				     			editor.jqxDropDownButton(\"close\");
				                   var ini = $('#jqxgridEditGlAccount').jqxGrid('getselectedrowindex');
				                    if(ini != -1){
				                        var item = $('#jqxgridEditGlAccount').jqxGrid('getrowdata', ini);
				                        var selectedPro = item.glAccountId;
				                        return selectedPro;	
				                    }
				                    return cellvalue;
				         	},
				       }
					 "/>          
<@jqGrid url="jqxGeneralServicer?organizationPartyId=${parameters.organizationPartyId}&sname=JQListFinAccountTypeGlAccount" dataField=dataField columnlist=columnlist
		 id="jqxgrid" filtersimplemode="true" showtoolbar="true" clearfilteringbutton="true" 
		 addColumns="finAccountTypeId;glAccountId;organizationPartyId[${parameters.organizationPartyId}]"
		 editable="true"  editrefresh="true" filterable="false"
		 deletesuccessfunction="OlbFinAccountTypeGlAccount.updateData" functionAfterAddRow="OlbFinAccountTypeGlAccount.updateData"
		 createUrl="jqxGeneralServicer?jqaction=C&sname=createFinAccountTypeGlAccount" alternativeAddPopup="alterpopupWindow" addrow="true" addType="popup"
		 addColumns="finAccountTypeId;glAccountId;organizationPartyId[${parameters.organizationPartyId}]"
		 updateUrl="jqxGeneralServicer?jqaction=U&sname=updateFinAccountTypeGlAccount"
		 editColumns="finAccountTypeId;glAccountId;organizationPartyId[${parameters.organizationPartyId}]"
		 deleterow="true" removeUrl="jqxGeneralServicer?sname=deleteFinAccountTypeGlAccount&jqaction=D" 
		 deleteColumn="finAccountTypeId;organizationPartyId[${parameters.organizationPartyId}]" 
 />	