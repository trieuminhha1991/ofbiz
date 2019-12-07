<#assign columnlist="{ text: '${uiLabelMap.DACustomerId}', dataField: 'partyId', width: '20%', 
						cellsrenderer: function(row, colum, value) {
                        	var data = $('#jqxgrid').jqxGrid('getrowdata', row);
                        	return \"<span><a href='javascript:void(0);' onclick='javascript:set_value(&#39;\" + data.partyId + \"&#39;)'>\" + data.partyId + \"</a></span>\";
                    	}
                     }, 
					 { text: '${uiLabelMap.DACustomerName}', dataField: 'groupName', width: '80%', 
					 	cellsrenderer: function(row, column, value){
					 		var data = $('#jqxgrid').jqxGrid('getrowdata', row);
					 		var dataStr = \"<span>\";
					 		if (data.lastName != null) {
					 			dataStr += data.lastName;
					 			if (data.middleName != null) dataStr += '&nbsp;' + data.middleName;
					 			if (data.middleName != null) dataStr += '&nbsp;' + data.firstName + \"</span>\";
				 			} else if (data.groupName != null) {
				 				dataStr += data.groupName;
				 			}
				 			dataStr += \"</span>\";
					 		return dataStr;
					 	}
					 }
					 "/>
<#assign dataField="[{ name: 'partyId', type: 'string'},
					{ name: 'partyTypeId', type: 'string'},
					{ name: 'groupName', type: 'string'},
					{ name: 'firstName', type: 'string'},
					{ name: 'lastName', type: 'string'},
					{ name: 'middleName', type: 'string'}
					]"/>
					
<@jqGrid filtersimplemode="true" filterable="true" addType="popup" dataField=dataField columnlist=columnlist usecurrencyfunction="true" viewSize="10" 
		 url="jqxGeneralServicer?sname=JQGetListCustomerGTOfCompany&productStoreId=${productStoreId?if_exists}" showtoolbar="false" width="95%"/>
<#--
<@jqGrid filtersimplemode="false" addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="true" showtoolbar="true" addrow="true" deleterow="true" alternativeAddPopup="alterpopupWindow" editable="false"
		 url="jqxGeneralServicer?sname=JQListAgreementGeographicalApplic&agreementId=${parameters.agreementId}&agreementItemSeqId=${parameters.agreementItemSeqId}"
		 createUrl="jqxGeneralServicer?sname=createAgreementGeographicalApplic&jqaction=C&agreementId=${parameters.agreementId}&agreementItemSeqId=${parameters.agreementItemSeqId}"
		 removeUrl="jqxGeneralServicer?sname=removeAgreementGeographicalApplic&jqaction=D&agreementId=${parameters.agreementId}&agreementItemSeqId=${parameters.agreementItemSeqId}"
		 updateUrl="jqxGeneralServicer?sname=updateAgreementGeographicalApplic&jqaction=U&agreementId=${parameters.agreementId}&agreementItemSeqId=${parameters.agreementItemSeqId}"
		 deleteColumn="agreementId[${parameters.agreementId}];agreementItemSeqId[${parameters.agreementItemSeqId}];geoId"
		 addColumns="agreementId[${parameters.agreementId}];agreementItemSeqId[${parameters.agreementItemSeqId}];geoId"
		 editColumns="agreementId[${parameters.agreementId}];agreementItemSeqId[${parameters.agreementItemSeqId}];geoId"
		 />
-->
