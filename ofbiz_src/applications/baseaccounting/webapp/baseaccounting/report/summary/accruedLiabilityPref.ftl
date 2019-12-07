<#assign dataField="[{ name: 'seqId', type: 'string' },
					 { name: 'productName', type: 'string'},
					 { name: 'uomId', type: 'string'},
					 { name: 'quantity', type: 'string'},
					 { name: 'unitCost', type: 'string'},
					 { name: 'amount', type: 'number'}
				 ]"/>

<#assign columnlist="{ text: '${uiLabelMap.BACCSeqId}', dataField: 'seqId', width: 100,
					 },
					 { text: '${uiLabelMap.BACCProductName}', dataField: 'productName',
					 },
					 { text: '${uiLabelMap.BACCUomId}', dataField: 'uomId', width: 150,
						 cellsrenderer: function(row, column, value){
							 for(var i = 0; i < uomData.length; i++){
								 if(value == uomData[i].uomId){
									 return '<span>' + uomData[i].description + '</span>';
								 }
							 }
							 return '<span>' + value + '</span>';
						 }
					 },
					 { text: '${uiLabelMap.BACCQuantity}', dataField: 'quantity', width: 100,
					 },
					 { text: '${uiLabelMap.BACCUnitCost}', dataField: 'unitCost', width: 150,
						 cellsrenderer: function(row, column, value){
							 var data = $('#accruedLiabilityPref').jqxGrid('getrowdata',row);
        		  			 if(data != undefined && data){
                		  		return '<span class=align-right>' + formatcurrency(value, data.currencyId) + '</span>';
        		  			 }
						 }
					 },
					 { text: '${uiLabelMap.BACCMoneyTotal}', dataField: 'amount', width: 200,
						 cellsrenderer: function(row, column, value){
							 var data = $('#accruedLiabilityPref').jqxGrid('getrowdata',row);
        		  			 if(data != undefined && data){
                		  		return '<span class=align-right>' + formatcurrency(value, data.currencyId) + '</span>';
        		  			 }
						 },
						 aggregates: ['sum'],
						 aggregatesrenderer: function (aggregates, column, element, summaryData) {
							 var renderstring = '<div class=\"jqx-widget-content jqx-widget-content-olbius\" style=\"float: left; width: 100%; height: 100%;\">';
							 $.each(aggregates, function (key, value) {
								 renderstring += '<div style=\"color: red; font-weight: bold; position: relative; margin: 6px; text-align: right; overflow: hidden;\"> ${uiLabelMap.BACCTotal}: ' + formatcurrency(value) + '</div>';
							 });
							 renderstring += '</div>';
							 return renderstring;
						 }
					 }
					 "/>

<#assign prefDate = StringUtil.replaceString(parameters.prefDate, "/", "-") />
<@jqGrid id="accruedLiabilityPref" filtersimplemode="true" filterable="false" addrefresh="true" editable="false" addType="popup" showtoolbar="false" clearfilteringbutton="true"
		 url="jqxGeneralServicer?sname=getAccruedLiabilityPref&dateStr=${prefDate}&organizationPartyId=${parameters.organizationPartyId}&partyId=${parameters.partyId}" dataField=dataField columnlist=columnlist showstatusbar="false"
		 statusbarheight="30" columngrouplist=columngrouplist showstatusbar="true"
	 />