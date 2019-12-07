<div id="jqxgridPaymentTax"></div>
<script>
	var OLBPayApplTax = function(){};
	
	OLBPayApplTax.getPaymentTaxData = function(){
		var paymentTaxData = new Array();
		var paymentTaxData  = [
	  	 	<#if paymentApplicationsTax?has_content>
	  	 		<#list paymentApplicationsTax as item>
	  	 			{
	  					'paymentApplicationId' : '${item.paymentApplicationId?if_exists}',
	  					'paymentId' : '${item.paymentId?if_exists}',
	  					'taxAuthGeoId' : '${item.taxAuthGeoId?if_exists}',
	  					'amountApplied' : '${item.amountApplied?if_exists}',
	  					'currencyUomId' : '${item.currencyUomId?if_exists}'
	  	 			},
	  	 		</#list>
	  	 	</#if>
	 	];
	  	return paymentTaxData;
	}
	
	OLBPayApplTax.prototype.initGrid = function(){
		//Create Payment Appl Pay
		var paymentTax = OLBPayApplTax.getPaymentTaxData();
		var sourcePaymentTax =
		{
		    localdata: paymentTax,
		    datatype: "array",
		    datafields:
		    [
		        { name: 'taxAuthGeoId', type: 'string' },
		        { name: 'amountApplied', type: 'string' },
		        { name: 'paymentApplicationId', type: 'string' },
		        { name: 'paymentId', type: 'string' }
		    ],
		    deleterow: function (rowid, commit) {
		    	var data = $('#jqxgridPaymentTax').jqxGrid('getrowdata', rowid);
		    	var submitData = {};
		    	submitData['paymentApplicationId'] = data.paymentApplicationId;
		    	submitData['paymentId'] = data.paymentId;
		    	submitData['taxAuthGeoId'] = data.taxAuthGeoId;
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
						  if(typeof sourcePaymentTax != 'undefined'){
							  sourcePaymentTax.localdata = getPosibleInvData();
							  $("#jqxgridPaymentTax").jqxGrid('updatebounddata');
						  }
						  sourcePaymentTax.localdata = getPaymentTaxData();
						  $("#jqxgridPaymentTax").jqxGrid('updatebounddata');
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
		var dataAdapterPaymentTax = new $.jqx.dataAdapter(sourcePaymentTax);
		
		$("#jqxgridPaymentTax").jqxGrid(
		{
		    width: '100%',
		    source: dataAdapterPaymentTax,
		    columnsresize: true,
		    pageable: true,
		    autoheight: true,
		    showtoolbar: true,
		    theme: 'olbius',
		    rendertoolbar: function (toolbar) {
	         	var container = $("<div id='toolbarcontainer' class='widget-header'></div>");
	             toolbar.append(container);
	             container.append('<h4>${uiLabelMap.BACCListPaidTaxes}</h4>');
	 	    },
		    localization: getLocalization(),
		    columns: [
		      { text: '${uiLabelMap.BACCTaxAuthGeoId}', datafield: 'taxAuthGeoId', width: 200 },
		      { text: '${uiLabelMap.BACCAmountApplied}', datafield: 'amountApplied',
		    	  cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
		      		  var data = $('#jqxgridPaymentInv').jqxGrid('getrowdata', row);
		    		  return '<span>' + formatcurrency(data.amountApplied,data.currencyUomId) + '</span>';
		      	  }  
		      },
		      { text: '${uiLabelMap.BACCDelete}', 
		    	  cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
		    		  return '<span><a onclick=' + "$('#jqxgridPaymentTax').jqxGrid('deleterow'," + row + ')' + '><i class="fa fa-trash-o"></i></a></span>';
		      	  }  
		      }
		    ]
		});
	}
</script>