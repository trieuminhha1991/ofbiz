<#assign dataField="[{ name: 'cardType', type: 'string' },
					 { name: 'glAccountId', type: 'string' },
					 { name: 'accountCode', type: 'string' },
					 { name: 'accountName', type: 'string' }
					 ]
					"/>
<#assign columnlist="{ text: '${uiLabelMap.BACCCardType}', datafield: 'cardType',editable: false, cellsrenderer:function (row, column, value) {
							var data = $('#jqxgrid').jqxGrid('getrowdata', row);
					        for(i=0;i < dataCTGL.length; i++){
								if(typeof(data) != 'undefined' && dataCTGL[i].enumId == data.cardType){
									return '<span>' + dataCTGL[i].description + '</span>';
								}
					        }
					        return '<span>' + data.cardType + '</span>';
					    }
				    },
					 { text: '${uiLabelMap.BACCGLAccountId}', datafield: 'glAccountId', columntype: 'template',width : '400',
					 	createeditor: function (row, cellvalue, editor, celltext, cellwidth, cellheight) {
					 	editor.append('<div id=\"jqxgridGlAccountEdit\"></div>');
					 	initDropDown(editor,$('#jqxgridGlAccountEdit'),400,400);
                        editor.jqxDropDownButton('setContent',cellvalue); 
                       },geteditorvalue : function(row,cellvalue,editor){
			     			editor.jqxDropDownButton(\"close\");
			                   var ini = $('#jqxgridGlAccountEdit').jqxGrid('getselectedrowindex');
			                    if(ini != -1){
			                        var item = $('#jqxgridGlAccountEdit').jqxGrid('getrowdata', ini);
			                        var selectedPro = item.glAccountId;
			                        return selectedPro;	
			                    }
			                    return cellvalue;
			         	},cellsrenderer : function(row){
			         		var data = $('#jqxgrid').jqxGrid('getrowdata',row);
			         		if(typeof(data) != 'undefined'){
			         			var code = data.accountCode ? data.accountCode : '';
			         			return '<span>' + code+ '</span>';
			         		}
			         		return '';
			         	}
					 },
					 {text : '${uiLabelMap.BACCAccountName}',datafield : 'accountName',width : '30%',editable : false}
					"/>
<@jqGrid url="jqxGeneralServicer?organizationPartyId=${parameters.organizationPartyId}&sname=JQListCreditCardTypeGlAccount" dataField=dataField columnlist=columnlist
		 deletesuccessfunction="OlbGlAccountTypeDefault.updateData" functionAfterAddRow="OlbGlAccountTypeDefault.updateData"
		 id="jqxgrid" filtersimplemode="true" showtoolbar="true" clearfilteringbutton="true"
		 addrow="true" addType="popup" alternativeAddPopup="alterpopupWindow"
		 createUrl="jqxGeneralServicer?organizationPartyId=${parameters.organizationPartyId}&jqaction=C&sname=createCreditCardTypeGlAccount"
		 addColumns="cardType;glAccountId;organizationPartyId[${parameters.organizationPartyId}]"
		 editable="true" editrefresh="true"
		 updateUrl="jqxGeneralServicer?sname=updateCreditCardTypeGlAccount&jqaction=U&organizationPartyId=${parameters.organizationPartyId}" 
		 editColumns="cardType;glAccountId;organizationPartyId[${parameters.organizationPartyId}]"
		 deleterow="true" removeUrl="jqxGeneralServicer?sname=deleteCreditCardTypeGlAccount&jqaction=D" 
		 deleteColumn="cardType;organizationPartyId[${parameters.organizationPartyId}]"
 />	