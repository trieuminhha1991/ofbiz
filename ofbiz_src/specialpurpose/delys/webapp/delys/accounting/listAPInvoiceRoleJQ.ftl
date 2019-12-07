 <#assign columnlist="{ text: '${uiLabelMap.FormFieldTitle_organizationPartyId}', dataField: 'partyId',width : '10%',cellsrenderer:
	                 	function(row, colum, value)
	                    {
	                    	var data = $('#jqxgrid3').jqxGrid('getrowdata',row);
                    		return \"<span><a href='/partymgr/control/viewprofile?partyId=\" + value + \"'>\" + value + \"</span>\";
	                    }},
	                  { text: '${uiLabelMap.FormFieldTitle_name}', dataField: 'groupName',width : '40%',cellsrenderer : function(row){
	                  		var data = $('#jqxgrid3').jqxGrid('getrowdata',row);
	                    	var name = data.groupName ? data.groupName : (data.fullName ? data.fullName : '');
                    		return \"<span>\" + name + \"</span>\";
	                  }},
                     { text: '${uiLabelMap.roleTypeId}', dataField: 'desRole',width : '25%'},
                     { text: '${uiLabelMap.FormFieldTitle_percentage}', dataField: 'percentage',filtertype : 'number',width  : '10%'},                     
					 { text: '${uiLabelMap.FormFieldTitle_datetimePerformed}', datafield: 'datetimePerformed',filtertype : 'range', cellsformat: 'dd/MM/yyyy'}
                      "/>
<#assign dataField="[
					{ name: 'partyId', type: 'string' },
					{ name: 'groupName', type: 'string' },
					{ name: 'fullName', type: 'string' },
					{ name: 'desRole', type: 'string' },
                 { name: 'name', type: 'string' },
                 { name: 'roleTypeId', type: 'string' },
                 { name: 'percentage', type: 'number' }, 
                 { name: 'datetimePerformed', type: 'date',other : 'Timestamp'}
                 ]
		 		 "/>	
<@jqGrid url="jqxGeneralServicer?invoiceId=${parameters.invoiceId}&sname=JQListAPInvoiceRole" dataField=dataField columnlist=columnlist filterable="true" filtersimplemode="false"
		 id="jqxgrid3" customTitleProperties="${uiLabelMap.AccountingInvoiceRoles}" clearfilteringbutton="true"/>