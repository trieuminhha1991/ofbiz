<script>
	var cellclass = function (row, columnfield, value) {
		var data = $('#jqxgridPayment').jqxGrid('getrowdata', row);
	    if (data.statusId == 'PMNT_CANCELLED') {
	        return 'canceled';
	    }
	    else if (data.statusId == 'PMNT_NOT_PAID') {
	        return 'not-paid';
	    }
	    else if (data.statusId == 'PMNT_VOID'){
	    	return 'void';
	    }
	    else if (data.statusId == 'PMNT_CONFIRMED'){
	    	return 'confirmed';
	    }
	    else if (data.statusId == 'INVOICE_IN_PROCESS'){
	    	return 'inprocess';
	    }
	    else if (data.statusId == 'PMNT_RECEIVED'){
	    	return 'received';
	    }
	    else if (data.statusId == 'PMNT_SENT'){
	    	return 'sent';
	    }
	};
	var filterObjData = new Object();
</script>

<style>
	.canceled {
	    background-color: #ff6f4c !important;
	}
	.not-paid {
	    background-color: #ffd595 !important;
	}
	.void {
	    background-color: #00a9ff !important; 
	}
	.confirmed {
	    background-color: #00CC99 !important;
	}
	.inprocess {
	    background-color: #999966 !important;
	}
	.received {
	    background-color: #8bd8b3 !important;
	}
	.sent {
	    background-color: #0EAFAF !important;
	}
	.aggregates{
		font-weight: 600;
		text-align: right;
	}
</style>
<#assign dataField="[
						{ name: 'paymentId', type: 'string' },
						{ name: 'paymentCode', type: 'string'},
						{ name: 'paymentMethodId', type: 'string'},
						{ name: 'paymentTypeId', type: 'string'},
						{ name: 'statusId', type: 'string'},
						{ name: 'comments', type: 'string'},
						{ name: 'partyIdFrom', type: 'string'},
						{ name: 'partyIdTo', type: 'string'},
						{ name: 'partyCodeFrom', type: 'string'},
						{ name: 'partyCodeTo', type: 'string'},
						{ name: 'effectiveDate', type: 'date', other:'Timestamp'},
						{ name: 'amount', type: 'number'},
						{ name: 'paymentApplied', type: 'number'},
						{ name: 'openPayment', type: 'number'},
						{ name: 'currencyUomId', type: 'string'},
						{ name: 'fullNameTo', type: 'string'},
						{ name: 'fullNameFrom', type: 'string'},
					 ]"/>

<#assign columnlist="{ text: '${uiLabelMap.BACCPaymentId}', dataField: 'paymentCode', width: '10%', pinned: true,
						cellsrenderer: function(row, column, value){
							var data = $('#jqxgridPayment').jqxGrid('getrowdata', row);
                        	if('${businessType}' == 'AP'){
                        		return '<span><a href=ViewAPPayment?paymentId=' + data.paymentId + ' style=\"color: blue !important;\">' + data.paymentCode + '</a></span>';
                        	}else{
                        		return '<span><a href=ViewARPayment?paymentId=' + data.paymentId + ' style=\"color: blue !important;\">' + data.paymentCode + '</a></span>';
                        	}
						},
						cellclassname: cellclass
					 },"/>

<#if businessType == "AP">
	<#assign columnlist = columnlist + "{text: '${uiLabelMap.BACCPaymentFromParty}', dataField: 'fullNameFrom', width: '18%', pinned: true, cellclassname: cellclass},
					              		{text: '${uiLabelMap.BACCPaymentToParty}', width: '17%', dataField:'fullNameTo',  pinned: true, cellclassname: cellclass},
					              		{text: '${uiLabelMap.BSOrganizationId}', width: '11%', dataField:'partyCodeTo',  pinned: true, cellclassname: cellclass},
					              		 "/>
<#else>
	<#assign columnlist = columnlist + "{text: '${uiLabelMap.BACCPaymentFromParty}', dataField: 'fullNameFrom', width: '18%', pinned: true, cellclassname: cellclass},
										{text: '${uiLabelMap.BSOrganizationId}', width: '11%', dataField:'partyCodeFrom',  pinned: true, cellclassname: cellclass},
					              		{text: '${uiLabelMap.BACCPaymentToParty}', width: '17%', dataField:'fullNameTo',  pinned: true, cellclassname: cellclass},
					              		"/>
</#if>

<#assign columnlist= columnlist + "{ text: '${uiLabelMap.BACCPaymentMethodType}', width: '17%', dataField:'paymentMethodId', filtertype: 'checkedlist',
              		 	cellsrenderer: function(row, column, value){
              		 		for(var i = 0; i < paymentMethodData.length; i++){
								if(value == paymentMethodData[i].paymentMethodId){
									return '<span title=' + value + '>' + paymentMethodData[i].description + '</span>';
								}
							}
							return '<span>' + value + '</span>';
              		 	},
              		 	createfilterwidget: function (column, columnElement, widget) {
              		 		var uniqueRecords =  [];
				   				if(paymentMethodData && paymentMethodData.length  > 0){
				   					var filterBoxAdapter = new $.jqx.dataAdapter(paymentMethodData,
							                {
							                    autoBind: true
							                });
					                var uniqueRecords = filterBoxAdapter.records;
				   				}
				   				widget.jqxDropDownList({source: uniqueRecords, displayMember: 'description', valueMember : 'paymentMethodId'});
              		 	},
			   			cellclassname: cellclass
              		 },
	 				  { text: '${uiLabelMap.BACCPaymentTypeId}', dataField: 'paymentTypeId', width: '16%', filtertype: 'checkedlist',
						cellsrenderer: function(row, column, value){
							for(var i = 0; i < dataPaymentType.length; i++){
								if(value == dataPaymentType[i].paymentTypeId){
									return '<span title=' + value + '>' + dataPaymentType[i].description + '</span>';
								}
							}
							return '<span>' + value + '</span>';
						},
						createfilterwidget: function (column, columnElement, widget) {
				   				var uniqueRecords =  [];
				   				if(dataPaymentType && dataPaymentType.length  > 0){
				   					var filterBoxAdapter = new $.jqx.dataAdapter(dataPaymentType,
							                {
							                    autoBind: true
							                });
					                var uniqueRecords = filterBoxAdapter.records;
				   				}
				   				widget.jqxDropDownList({source: uniqueRecords, displayMember: 'description', valueMember : 'paymentTypeId'});
		   			  	},
			   			cellclassname: cellclass
      		 		  },
      		 		  { text: '${uiLabelMap.BACCStatusId}', width: '13%', dataField:'statusId', filtertype: 'checkedlist',
						cellsrenderer: function(row, column, value){
							for(var i = 0; i < dataStatusType.length; i++){
								if(value == dataStatusType[i].statusId){
									return '<span title=' + value + '>' + dataStatusType[i].description + '</span>';
								}
							}
							return '<span>' + value + '</span>';
						},createfilterwidget: function (column, columnElement, widget) {
				   				var uniqueRecords2 = [] ;
				   				if(dataStatusType && dataStatusType.length > 0 ){
				   					var filterBoxAdapter2 = new $.jqx.dataAdapter(dataStatusType,
							                {
							                    autoBind: true
							                });
					                uniqueRecords2 = filterBoxAdapter2.records;
				   				}
				   				widget.jqxDropDownList({ filterable:true,source: uniqueRecords2, displayMember: 'description', valueMember : 'statusId'});			   				
 						},
 						cellclassname: cellclass
      		 		  },
      		 		  { text: '${uiLabelMap.BACCEffectiveDate}', dataField: 'effectiveDate', width: '12%', cellsformat:'dd/MM/yyyy', filtertype: 'range', cellclassname: cellclass},
      		 		  { text: '${uiLabelMap.BACCComment}', dataField: 'comments', width: 250,
	                	  cellclassname: cellclass
	 		  		  },
	 		  		  { text: '${uiLabelMap.BACCAmount}', dataField: 'amount', width: '14%', filterable: true, filtertype: 'number',
	                	  cellsrenderer: function(row, columns, value){
	                		  var data = $('#jqxgridPayment').jqxGrid('getrowdata',row);
	                		  return '<span class=align-right>'+formatcurrency(value, data.currencyUomId)+'</span>';
	                	  },
	                	  cellclassname: cellclass,
	                	  aggregatesrenderer: function (aggregates, column, element, summaryData){
                		     var renderstring = '<div class=aggregates>'
                             $.each(aggregates, function (key, value) {
	                                renderstring += key + ' : ' + formatcurrency(value);
                             });                          
                		     renderstring += '</div>';
                             return renderstring; 
	                	  },
	                	  aggregates: [{ '${uiLabelMap.BACCAmountTotal}':
	                		    function (aggregatedValue, currentValue) {
	                		        if (currentValue) {
	                		            return aggregatedValue + currentValue;
	                		        }
	                		        return aggregatedValue;
	                		    }
	                	  }]
	  		  		  },
	                  { text: '${uiLabelMap.BACCAppliedPayments}', dataField: 'paymentApplied', width: '14%', filterable: true, filtertype: 'number', sortable : true,
	                	  cellsrenderer: function(row, columns, value){
	                		  var data = $('#jqxgridPayment').jqxGrid('getrowdata',row);
	                		  return '<span class=align-right>'+formatcurrency(value, data.currencyUomId)+'</span>';
	                	  },
	                	  cellclassname: cellclass,
	                	  aggregatesrenderer: function (aggregates, column, element, summaryData){
                		     var renderstring = '<div class=aggregates>'
                             $.each(aggregates, function (key, value) {
	                                renderstring += key + ' : ' + formatcurrency(value);
                             });                          
                		     renderstring += '</div>';
                             return renderstring; 
	                	  },
	                	  aggregates: [{ '${uiLabelMap.BACCAmountTotal}':
	                		    function (aggregatedValue, currentValue) {
	                		        if (currentValue) {
	                		            return aggregatedValue + currentValue;
	                		        }
	                		        return aggregatedValue;
	                		    }
	                	  }]
	                  },
	                  { text: '${uiLabelMap.BACCOpenPayments}', dataField: 'openPayment', width: '14%', filterable: false, sortable : false,
	                	  cellsrenderer: function(row, columns, value){
	                		  var data = $('#jqxgridPayment').jqxGrid('getrowdata',row);
	                		  var newValue = data.amount - data.paymentApplied;
	                		  return '<span class=align-right>'+formatcurrency(newValue, data.currencyUomId)+'</span>';
	                	  },
	                	  cellclassname: cellclass
	                  }
					 "/>

<@jqGrid id="jqxgridPayment" filtersimplemode="true"  
		 editable="false" addType="popup" showtoolbar="true" clearfilteringbutton="true" addrow="true" alternativeAddPopup="newPaymentPopup"
		 url="jqxGeneralServicer?sname=JqxGetListPaymentsNew&paymentType=${businessType}" dataField=dataField columnlist=columnlist 
		 showstatusbar="false" statusbarheight="30" jqGridMinimumLibEnable="false"
		 customcontrol1="fa fa-file-excel-o open-sans@${StringUtil.wrapString(uiLabelMap.BSExportExcel)}@javascript:void(0);exportExcel()"
		 isSaveFormData="true" formData="filterObjData"
		 />
                     
<script type="text/javascript">
var exportExcel = function(){
	var winName='ExportExcel';
	var winURL = 'exportListPaymentExcel';
	var form = document.createElement("form");
	form.setAttribute("method", "post");
	form.setAttribute("action", winURL);
	form.setAttribute("target", "_blank");
	var params = filterObjData.data != "undefined"? filterObjData.data : {};
	params.paymentType = "${businessType}";
	for(var key in params){
		if (params.hasOwnProperty(key)) {
			var input = document.createElement('input');
			input.type = 'hidden';
			input.name = key;
			input.value = params[key];
			form.appendChild(input);
		}
	}
	document.body.appendChild(form);
	window.open(' ', winName);
	form.target = winName;
	form.submit();                 
	document.body.removeChild(form);
}
</script>