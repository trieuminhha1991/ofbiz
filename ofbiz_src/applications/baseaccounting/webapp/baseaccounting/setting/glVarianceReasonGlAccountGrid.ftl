<#assign dataField="[{ name: 'varianceReasonId', type: 'string' },
					 { name: 'glAccountId', type: 'string'},
					 { name: 'accountCode', type: 'string'},
					 { name: 'accountName', type: 'string'}
					 ]
					 "/>

<#assign columnlist="{ text: '${uiLabelMap.FormFieldTitle_varianceReasonId}',filtertype : 'checkedlist', datafield: 'varianceReasonId', cellsrenderer:function (row, column, value) {
					var data = $('#jqxgrid').jqxGrid('getrowdata', row);
			        for(i=0;i < VRGC.length; i++){
						if(typeof(data) != 'undefined' && VRGC[i].varianceReasonId == data.varianceReasonId){
							return '<span>' + VRGC[i].description + '</span>';
						}
			        }
			        return '<span>' + data.varianceReasonId + '</span>';
				    },createfilterwidget : function(column,columnElement,widget){
					    	var source = {
				    		localdata : VRGC,
				    		datatype : 'array'
				    	};
			    	var filterBoxAdapter = new $.jqx.dataAdapter(source,{autoBind : true});
			    	var uniRecords = filterBoxAdapter.records;
			    	widget.jqxDropDownList({source: uniRecords, displayMember: 'description', valueMember: 'varianceReasonId',placeHolder : '${StringUtil.wrapString(uiLabelMap.PleaseChooseAcc?default(''))}',renderer: function (index, label, value) 
									{
										for(i=0;i < VRGC.length; i++){
											if(VRGC[i].varianceReasonId == value){
												return VRGC[i].description;
											}
										}
									    return value;
									}
			    			});
			   		 }
			    },
				{ text: '${uiLabelMap.BACCAccountCode}', datafield: 'accountCode'},
				{ text: '${uiLabelMap.BACCAccountName}', datafield: 'accountName',width : '40%'} 
				 "/>
	
<@jqGrid filtersimplemode="true"  addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="true" showtoolbar="true" addrow="true" alternativeAddPopup="alterpopupWindow" deleterow="true" editable="false"
		 url="jqxGeneralServicer?sname=JQGetListVarianceReasonGlAccounts&organizationPartyId=${parameters.organizationPartyId}" 
		 createUrl="jqxGeneralServicer?sname=createVarianceReasonGlAccount&jqaction=C&organizationPartyId=${parameters.organizationPartyId}" addColumns="glAccountId;varianceReasonId;organizationPartyId[${parameters.organizationPartyId}]"
		 removeUrl="jqxGeneralServicer?sname=removeVarianceReasonGlAccount&jqaction=D&organizationPartyId=${parameters.organizationPartyId}" deleteColumn="varianceReasonId;organizationPartyId[${parameters.organizationPartyId}]"
		 updateUrl="jqxGeneralServicer?sname=updateVarianceReasonGlAccount&jqaction=U&organizationPartyId=${parameters.organizationPartyId}" editColumns="glAccountId;varianceReasonId;organizationPartyId[${parameters.organizationPartyId}]"
 		deletesuccessfunction="OlbVarianceReason.updateData" functionAfterAddRow="OlbVarianceReason.updateData"		
/>