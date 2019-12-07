<div id="jqxgridPaymentPay"></div>
<script>
	var OLBPayApplPay = function(){};
	
	OLBPayApplPay.getPaymentPayData = function(){
		//Set up data for jqxgridPaymentPay
		var payApplPayData = new Array();
		var payApplPayData  = [
      	 	<#if paymentApplicationsPay?has_content>
      	 		<#list paymentApplicationsPay as item>
      	 			{
      					'paymentApplicationId' : '${item.paymentApplicationId?if_exists}',
      					'paymentId' : '${item.paymentId?if_exists}',
      					'toPaymentId' : '${item.toPaymentId?if_exists}',
      					'amountApplied' : '${item.amountApplied?if_exists}',
      					'currencyUomId' : '${item.currencyUomId?if_exists}'
      	 			},
      	 		</#list>
      	 	</#if>
  	 	];
  		return payApplPayData;
	}
	
	OLBPayApplPay.prototype.initGrid = function(){
		//Create Payment Appl Pay
		var paymentPay = OLBPayApplPay.getPaymentPayData();
		var sourcePaymentPay =
		{
		    localdata: paymentPay,
		    datatype: "array",
		    datafields:
		    [
		        { name: 'toPaymentId', type: 'string' },
		        { name: 'amountApplied', type: 'string' },
		        { name: 'paymentApplicationId', type: 'string' },
		        { name: 'paymentId', type: 'string' }
		    ],
		    deleterow: function (rowid, commit) {
		    	var data = $('#jqxgridPaymentPay').jqxGrid('getrowdata', rowid);
		    	var submitData = {};
		    	submitData['paymentApplicationId'] = data.paymentApplicationId;
		    	submitData['paymentId'] = data.paymentId;
		    	submitData['toPaymentId'] = data.toPaymentId;
		    	submitData['amountApplied'] = data.amountApplied;
		    	$.ajax({
				  url: "removePaymentApplication",
				  type: "POST",
				  data: submitData,
				  async: false,
				  dataType: "json",
				  success: function(res) {
					  if(res._ERROR_MESSAGE_LIST_ || res._ERROR_MESSAGE_){
					  	  $('#container').empty();
						  $('#notification').jqxNotification({ width: "100%", appendContainer: "#container", opacity: 0.9, autoClose: true, template: "error" });
						  if(res._ERROR_MESSAGE_LIST_){
							  $('#notification').text(res._ERROR_MESSAGE_LIST_);
						  }
						  if(res._ERROR_MESSAGE_){
							  $('#notification').text(res._ERROR_MESSAGE_);
						  }
						  $('#notification').jqxNotification('open');
					  }else{
						  if(typeof sourcePaymentPay != 'undefined'){
							  sourcePaymentPay.localdata = getPosibleInvData();
							  $("#jqxgridPaymentPay").jqxGrid('updatebounddata');
						  }
						  sourcePaymentPay.localdata = getPaymentInvData();
						  $("#jqxgridPaymentPay").jqxGrid('updatebounddata');
						  $("#editPayment").jqxGrid('updatebounddata');
						  updateInvGridTitle();
						  $('#container').empty();
						  $("#notification").jqxNotification({ width: "100%", appendContainer: "#container", opacity: 0.9, autoClose: true, template: "info" });
						  $("#notification").text(res._EVENT_MESSAGE_);
						  $("#notification").jqxNotification('open');
					  }
				  }
			  	})
		        commit(true);
		    }
		};
		var dataAdapterPaymentPay = new $.jqx.dataAdapter(sourcePaymentPay);
		
		$("#jqxgridPaymentPay").jqxGrid(
		{
		    width: '100%',
		    source: dataAdapterPaymentPay,
		    columnsresize: true,
		    pageable: true,
		    autoheight: true,
		    theme:'olbius',
		    localization: getLocalization(),
		    showtoolbar: true,
		    rendertoolbar: function (toolbar) {
	         	var container = $("<div id='toolbarcontainer' class='widget-header'></div>");
	             toolbar.append(container);
	             container.append('<h4>${uiLabelMap.BACCListPaidPayments}</h4>');
	 	    },
		    columns: [
		      { text: '${uiLabelMap.BACCToPaymentId}', datafield: 'toPaymentId', width: 200 },
		      { text: '${uiLabelMap.BACCAmountApplied}', datafield: 'amountApplied',
		    	  cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
		      		  var data = $('#jqxgridPaymentPay').jqxGrid('getrowdata', row);
		    		  return '<span>' + formatcurrency(data.amountApplied,data.currencyUomId) + '</span>';
		      	  }  
		      },
		      { text: '${uiLabelMap.BACCDelete}', 
		    	  cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
		    		  return '<span><a onclick=' + "$('#jqxgridPaymentPay').jqxGrid('deleterow'," + row + ')' + '><i class="fa fa-trash-o"></i></a></span>';
		      	  }  
		      }
		    ]
		});
	}
</script>