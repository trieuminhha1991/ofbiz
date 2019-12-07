<#include "invoiceViewApplApply.ftl" />
<div id="containerjqxgridInvAppl" style="background-color: transparent; overflow: auto; width: 100%;"></div>
<div id="jqxNotificationjqxgridInvAppl">
    <div id="notificationContentjqxgridInvAppl">
    </div>
</div>
<div id="jqxgridInvAppl"></div>
<script>

	JQXGridInvAppl = function (){
		//Prepare Data
		<#assign listPaymentTypes = delegator.findByAnd("PaymentType", null, Static["org.ofbiz.base.util.UtilMisc"].toList("paymentTypeId DESC"), false)>
		dataPaymentType = [
		   <#if listPaymentTypes?exists>
		       	<#list listPaymentTypes as type>
		       		{
		       			paymentTypeId : "${type.paymentTypeId}",
		       			description : "${StringUtil.wrapString(type.get('description',locale))}"
					},
		   		</#list>
			</#if>
		];
	};
	
	JQXGridInvAppl.prototype.initGrid = function(){
		//Data fields 
		var datafields = [{ name: 'paymentId', type: 'string'},
		                  { name: 'paymentCode', type: 'string'},
		                  { name: 'paymentTypeId', type: 'string'},
		                  { name: 'total', type: 'number'},
		                  { name: 'paymentApplicationId', type: 'string'},
		                  { name: 'amountApplied', type: 'number'}
	                  ];
		//Column of grid
		var columnlist = [{ text: '${uiLabelMap.BACCPaymentId}', datafield: 'paymentCode', width: 150,
		               	   cellsrenderer: function (row, columns, value, defaulthtml, columnproperties, rowdata) {
		               		   if(!value){
		               			   value = rowdata.paymentId;
		               		   }
		               		   if('${businessType}' == 'AP'){
		               			   return '<span> <a title=' + value + ' href=' +'ViewAPPayment?paymentId=' + rowdata.paymentId + '>' + value + '</a> </span>';
		               		   }else{
		               			   return '<span> <a title=' + value + ' href=' +'ViewARPayment?paymentId=' + rowdata.paymentId + '>' + value + '</a> </span>';
		               		   }
		       				},   
		                  },
		                  { text: '${uiLabelMap.BACCPaymentTypeId}', dataField: 'paymentTypeId', width: 400, 
		                	  cellsrenderer: function(row, column, value){
								for(var i = 0; i < dataPaymentType.length; i++){
									if(value == dataPaymentType[i].paymentTypeId){
										return '<span title=' + value + '>' + dataPaymentType[i].description + '</span>';
									}
								}
								return '<span>' + value + '</span>';
		                	  }
		                  },
		                  /*{ text: '${uiLabelMap.BACCTotal}', dataField: 'total',filtertype : 'number', width: 400, 
		       	    	   	cellsrenderer: function(row, colum, value){
	                     		return '<span>' + formatcurrency(value,'${invoice.currencyUomId?if_exists}') + '</span>';	
	                 		}
		                  }, */
		                  { text: '${uiLabelMap.BACCAmountApplied}', datafield: 'amountApplied', filtertype : 'number',
		               	   	cellsrenderer: function(row, colum, value){
	                    		return '<span>' + formatcurrency(value,'${invoice.currencyUomId?if_exists}') + '</span>';	
	                		}   
		                  },
	  					];
		//Tool bar of grid
		<#assign title = uiLabelMap.BACCAppliedPayments + " <span class='custom' id='appliedAmountInv'>" +  Static["org.ofbiz.base.util.UtilFormatOut"].formatCurrency(appliedAmount?if_exists?double, invoice.currencyUomId!"VND", locale, 2) + "</span> /" + uiLabelMap.BACCOpenPayments + " <span class='custom' id='notAppliedAmountInv'>" + Static["org.ofbiz.base.util.UtilFormatOut"].formatCurrency(notAppliedAmount?if_exists?double, invoice.currencyUomId!"VND", locale, 2) + "</span>" >
		var rendertoolbar = function (toolbar){
			var container = $("<div id='toolbarcontainer' class='widget-header'></div>");
            toolbar.append(container);
            container.append("<h4>${title}</h4>");
	   	}
		//Configuration for grid
		var config = {
	   		width: '100%', 
	   		virtualmode: true,
	   		showfilterrow: true,
	   		showtoolbar: true,
	   		rendertoolbar: rendertoolbar,
	   		selectionmode: 'singlerow',
	   		pageable: true,
	   		sortable: false,
	        filterable: false,
	        editable: false,
	        rowsheight: 26,
	        selectionmode: 'singlerow',
	        url: 'JqxGetListInvoiceApplications&invoiceId=${parameters.invoiceId}'
	   	};
		//Create grid
	   	Grid.initGrid(config, datafields, columnlist, null, $("#jqxgridInvAppl"));
	}
</script>
<style>
	.custom{
		color: red;
	  	font-weight: bold;
		font-size: 16px;
	  	font-style: italic;
	}
</style>