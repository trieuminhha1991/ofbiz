<#assign dataField="[{ name: 'glAccountId', type: 'string' },
					 { name: 'accountCode', type: 'string' },					 
					 { name: 'glAccountTypeId', type: 'string'},
					 { name: 'accountName', type: 'string' }
					 ]
					"/>
<#assign columnlist="
	{ text: '${uiLabelMap.FormFieldTitle_glAccountType}',filtertype : 'checkedlist', datafield: 'glAccountTypeId', cellsrenderer:function (row, column, value) {
			var data = $('#jqxgrid').jqxGrid('getrowdata', row);
	        for(i=0;i < dataGATP.length; i++){
				if(typeof(data) != 'undefined' && dataGATP[i].glAccountTypeId == data.glAccountTypeId){
					return '<span>' + dataGATP[i].description + '</span>';
				}
	        }
	        return '';
	    },createfilterwidget : function(column,columnElement,widget){
	    	var source = {
	    		localdata : dataGATP,
	    		datatype : 'array'
	    	};
	    	var filterBoxAdapter = new $.jqx.dataAdapter(source,{autoBind : true});
	    	var uniRecords = filterBoxAdapter.records;
	    	widget.jqxDropDownList({filterable : true,source: uniRecords, displayMember: 'description', valueMember: 'glAccountTypeId',placeHolder : '${StringUtil.wrapString(uiLabelMap.BACCPleaseChooseAcc?default(''))}',renderer: function (index, label, value) 
					{
						for(i=0;i < dataGATP.length; i++){
							if(dataGATP[i].glAccountTypeId == value){
								return dataGATP[i].description;
							}
						}
					    return value;
					}
	    		});
	    }
    },
    { text: '${uiLabelMap.BACCAccountCode}', datafield: 'accountCode',width : '10%',cellsrenderer : function(row){
			var data = $('#jqxgrid').jqxGrid('getrowdata',row);
			if(typeof(data) != 'undefined' && data.accountCode != null){
				return '<span class=\"custom-style-word\">' + data.accountCode + '</span>';
			}
			return '';
	 	}
	 } ,   
     { text: '${uiLabelMap.BACCAccountName}', datafield: 'accountName',cellsrenderer : function(row){
     			var data = $('#jqxgrid').jqxGrid('getrowdata',row);
     			if(typeof(data) != 'undefined' && data.accountName != null){
     				return '<span class=\"custom-style-word\">' + data.accountName + '</span>';
     			}
     			return '';
    		 }
   		}
		"/>		
	 
 <@jqGrid url="jqxGeneralServicer?organizationPartyId=${parameters.organizationPartyId}&sname=JQListGLAccountTypeDedault" columnlist=columnlist dataField=dataField
	  filterable="true" clearfilteringbutton="true" showtoolbar="true" addrow="true" alternativeAddPopup="alterpopupWindow" deleterow="true" deletesuccessfunction="OlbGlAccountTypeDefault.updateData" functionAfterAddRow="OlbGlAccountTypeDefault.updateData"
	 createUrl="jqxGeneralServicer?organizationPartyId=${parameters.organizationPartyId}&jqaction=C&sname=createGlAccountTypeDefault"
	 removeUrl="jqxGeneralServicer?organizationPartyId=${parameters.organizationPartyId}&jqaction=D&sname=removeGlAccountTypeDefault"
	 deleteColumn="glAccountId;glAccountTypeId;organizationPartyId[${parameters.organizationPartyId}]"
	 addColumns="glAccountId;glAccountTypeId;organizationPartyId[${parameters.organizationPartyId}]" addType="popup" id="jqxgrid"/>