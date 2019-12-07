<script>
	<#assign listPaymentType = delegator.findList("PaymentType", null, null, null, null, false) />
	var listPaymentType = [
                   	<#if listPaymentType?exists && listPaymentType?has_content>
                   		<#list listPaymentType as paymentType>
                   		<#assign des = paymentType.get("description",locale) !>
                   			{
                   				paymentTypeId : '${paymentType.paymentTypeId?if_exists}',
                   				description : '${StringUtil.wrapString(des?default(''))}'
                   			},
                   		</#list>
                   	</#if>	
                   	];
	
	//Prepare for Status data
	<#assign statusList = delegator.findList("StatusItem", null, null, null, null, false) />
	statusData = [
	              <#list statusList as item>
					<#assign description = StringUtil.wrapString(item.description?if_exists) />
					{'statusId': '${item.statusId}', 'description': '${description}'},
				  </#list>
				];
</script>
<#assign dataField="[{ name: 'paymentId', type: 'string'},
                 	{ name: 'partyIdFrom', type: 'string'},
                 	{ name: 'partyIdTo', type: 'string'},
                 	{ name: 'paymentTypeId', type: 'string'},
                 	{ name: 'statusId', type: 'string'},
                 	{ name: 'amount', type: 'number'},
                 	{ name: 'currencyUomId', type: 'string'},
                 	{ name: 'fromDate', type: 'date'},
                 	{ name: 'thruDate', type: 'date'},
		 		  	]"/>

<#assign columnlist="{ text: '${uiLabelMap.paymentId}', dataField: 'paymentId', width: 100},
					 { text: '${uiLabelMap.partyIdFrom}', dataField: 'partyIdFrom', width: 150,
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
					 { text: '${uiLabelMap.partyIdTo}', dataField: 'partyIdTo', width: 150,
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
					 { text: '${uiLabelMap.paymentTypeId}', dataField: 'paymentTypeId', width: 150,
						 cellsrenderer : function (row, column, value) {
	        				for(i = 0; i < listPaymentType.length; i++){
		        				if(listPaymentType[i].paymentTypeId == value){
		        					return '<span>' + listPaymentType[i].description + '</span>';
	        					}
	        				}
	        				return '';
	    				}
					 },
					 { text: '${uiLabelMap.statusId}', dataField: 'statusId', width: 150,
						 cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
	 							for(var i = 0; i < statusData.length; i++){
	 								if(value == statusData[i].statusId){
	 									return '<span title=' + value + '>' + statusData[i].description + '</span>';
	 								}
	 							}
	 							return '<span> ' + value + '</span>';
 						}
					 },
					 { text: '${uiLabelMap.amount}', dataField: 'amount', width: 150,
						 cellsrenderer: function(row, column, value){
							 var data = $('#jqxgrid').jqxGrid('getrowdata', row);
							 return '<span>' + formatcurrency(data.amount,data.currencyUomId) +'</span>';
						 }
					 },
					 { text: '${uiLabelMap.fromDate}', dataField: 'fromDate', width: 150, cellsformat: 'dd/MM/yyyy',columntype:'datetimeinput'},
                     { text: '${uiLabelMap.thruDate}', dataField: 'thruDate', width: 150, cellsformat: 'dd/MM/yyyy',columntype:'datetimeinput'},
                     "/>
<@jqGrid isShowTitleProperty="true" filtersimplemode="true" addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="true" showstatusbar="false" showtoolbar="true" addrow="false" filterable="true" editable="false"
		 url="jqxGeneralServicer?sname=JQApListPaymentGroupMember&paymentGroupId=${parameters.paymentGroupId}"
		/>
<style type="text/css">
	#jqxgrid{
		padding-left: 20px;
	}
	.jqx-grid-statusbar-olbius{
		display: none;
	}
</style>