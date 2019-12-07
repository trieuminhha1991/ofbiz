<#assign dataField="[{ name: 'taxAuthPartyId', type: 'string'},
					 { name: 'taxAuthGeoId', type: 'string'},
					 { name: 'glAccountId', type: 'string'},
					 { name: 'organizationPartyId', type: 'string'},
					 { name: 'geoName', type: 'string'},
					 { name: 'accountName', type: 'string'},
					 { name: 'accountCode', type: 'string'}
					]"/>

<#assign columnlist="{ text: '${uiLabelMap.BACCTaxAuthPartyId}', datafield: 'taxAuthPartyId', editable: false, width: 200,cellsrenderer : function(row){
								var data = $('#jqxgrid').jqxGrid('getrowdata',row);
								return '<span class=\"custom-style-word\">' +data.taxAuthPartyId + '</span>';
							}
						},
					 { text: '${uiLabelMap.BACCTaxAuthGeoId}', datafield: 'taxAuthGeoId', editable: false, cellsrenderer:listTAGRenderer,width: 350},
					 { text: '${uiLabelMap.BACCGLAccountId}', datafield: 'glAccountId', columntype: 'template',width : 450, cellsrenderer:listGlAccountOrganizationAndClassRenderer,
					 	createeditor: function (row, column, editor) {
					 		editor.append('<div id=\"jqxgridEditor\"></div>');
					 		action.initDropDown(editor,$(\"#jqxgridEditor\"),{wgrid : 450,wbutton : 450,dropDownHorizontalAlignment  : false });
                        },
                       geteditorvalue : function(row,cellvalue,editor){
			     			editor.jqxDropDownButton(\"close\");
			                   var ini = $('#jqxgridEditor').jqxGrid('getselectedrowindex');
			                    if(ini != -1){
			                        var item = $('#jqxgridEditor').jqxGrid('getrowdata', ini);
			                        var selectedPro = item.glAccountId;
			                        return selectedPro;	
			                    }
			                    return cellvalue;
			         	}
                      },
			         	{text : '${uiLabelMap.BACCAccountName}',datafield : 'accountName',editable : false}
					"/>
					

<@jqGrid  filtersimplemode="true" editrefresh="true" deletesuccessfunction=updateListAfterDel addType="popup" dataField=dataField columnlist=columnlist addrefresh="true" clearfilteringbutton="true" showtoolbar="true" addrow="true" deleterow="true" editable="true" alternativeAddPopup="alterpopupWindow"
		 url="jqxGeneralServicer?sname=JQListTaxAuthorityGLAccounts&organizationPartyId=${parameters.organizationPartyId}"
		 removeUrl="jqxGeneralServicer?sname=deleteTaxAuthorityGlAccount&jqaction=D&organizationPartyId=${parameters.organizationPartyId}"
		 updateUrl="jqxGeneralServicer?sname=updateTaxAuthorityGlAccount&jqaction=U&organizationPartyId=${parameters.organizationPartyId}"
		 createUrl="jqxGeneralServicer?sname=createTaxAuthorityGlAccount&jqaction=C&organizationPartyId=${parameters.organizationPartyId}"
		 editColumns="taxAuthPartyId;taxAuthGeoId;glAccountId;organizationPartyId[${parameters.organizationPartyId}]"
		 deleteColumn="taxAuthPartyId;taxAuthGeoId;glAccountId;organizationPartyId[${parameters.organizationPartyId}]"
		 addColumns="taxAuthPartyId;taxAuthGeoId;glAccountId;organizationPartyId[${parameters.organizationPartyId}]" 
		 />
	 