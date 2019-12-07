<div class="row-fluid wizard-actions" style = "margin-top: 20px;">
	<button class="btn btn-small btn-danger" id="btnCancel">
		<i class="icon-trash"></i>
		${uiLabelMap.BACCCancel}
	</button>
	<button class="btn btn-small btn-success" id="btnOK">
		<i class="fa fa-check"></i>
		${uiLabelMap.BACCOK}
	</button>
</div>
<script>
	var OLBCtrl = function(){
		
	}
	OLBCtrl.prototype.bindEvent = function(){
		$('#btnOK').on('click', function(){
			var submitedData = {};
			submitedData['prepaidExpId'] = $('#prepaidExpId').val();
			submitedData['prepaidExpName'] = $('#prepaidExpName').val();
			submitedData['acquiredDate'] = $('#acquiredDate').jqxDateTimeInput('getDate').getTime();
			submitedData['amount'] = $('#amount').val();
			submitedData['allocPeriodNum'] = $('#allocPeriodNum').val();
			submitedData['amountEachPeriod'] = $('#amountEachPeriod').val();
			submitedData['prepaidExpGlAccountId'] = $('#prepaidExpGlAccountId').attr('data-value');
			submitedData['description'] = $('#description').val();
			submitedData['listAllocs'] = JSON.stringify(alloc.attr.ITEM_DATA);
			//Send Request Create
			$.ajax({
				  url: "createPrepaidExpAndAlloc",
				  type: "POST",
				  data: submitedData,
				  async: false,
				  success: function(data) {
					  if(data.responseMessage == 'success'){
							window.location.replace('<@ofbizUrl>ListPrepaidExps?organizationPartyId=${parameters.organizationPartyId}</@ofbizUrl>');
						}else if(data.responseMessage == 'error'){
							accutils.confirm.dialog(data.errorMessage, function(){}, '${uiLabelMap.wgcancel}', '${uiLabelMap.wgok}');
						}
				  }
		  	});
		});
	}
</script>