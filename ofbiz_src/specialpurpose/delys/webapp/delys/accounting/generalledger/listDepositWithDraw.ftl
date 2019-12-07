<script>
</script>
<#assign columnlist="{ text: '${uiLabelMap.paymentId}', dataField: 'paymentId', editable: false},
                     { text: '${uiLabelMap.paymentTypeDesc}', dataField: 'paymentTypeDesc', editable: false},
                     { text: '${uiLabelMap.partyIdFrom}', dataField: 'partyIdFrom', editable: false,
						cellsrenderer: function(row, column, value){
							  var partyName = value;
							  $.ajax({
									url: 'getPartyName',
									type: 'POST',
									data: {partyId: value},
									dataType: 'json',
									async: false,
									success : function(data) {
										if(!data._ERROR_MESSAGE_){
											partyName = data.partyName;
										}
							        }
								});
							  return '<span title' + value + '>' + partyName + '</span>';
						 }
                     },
                     { text: '${uiLabelMap.partyIdTo}', dataField: 'partyIdTo', editable: false,
                    	 cellsrenderer: function(row, column, value){
							  var partyName = value;
							  $.ajax({
									url: 'getPartyName',
									type: 'POST',
									data: {partyId: value},
									dataType: 'json',
									async: false,
									success : function(data) {
										if(!data._ERROR_MESSAGE_){
											partyName = data.partyName;
										}
							        }
								});
							  return '<span title' + value + '>' + partyName + '</span>';
						 }
                     },
					 { text: '${uiLabelMap.amount}', dataField: 'amount', editable: false, 
                    	 cellsrenderer: function(row, column, value){
                    		 var data = $('#jqxgrid').jqxGrid('getrowdata', row);
                    		 return '<span>' + formatcurrency(data.amount,data.currencyUomId) + '</span>';
                    	 }
					 },
					 { text: '${uiLabelMap.effectiveDate}', dataField: 'effectiveDate', editable: false, cellsformat: 'dd/MM/yyyy'}
                     "/>

<#assign dataField="[{ name: 'paymentId', type: 'string' },
                 	{ name: 'paymentTypeDesc', type: 'string' },
                 	{ name: 'partyIdFrom', type: 'string' },
                 	{ name: 'partyIdTo', type: 'string' },
                 	{ name: 'amount', type: 'number' },
                 	{ name: 'currencyUomId', type: 'string' },
                 	{ name: 'effectiveDate', type: 'date' }
                 	]
		 		  	"/>
<@jqGrid filtersimplemode="true" usecurrencyfunction="true" addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="true" showtoolbar="true" addrow="false" deleterow="false" filterable="true" alternativeAddPopup="alterpopupWindow" editable="true"
		 url="jqxGeneralServicer?sname=JQListDepositWithDraw&finAccountId=${parameters.finAccountId}&organizationPartyId=${defaultOrganizationPartyId}"
		/>