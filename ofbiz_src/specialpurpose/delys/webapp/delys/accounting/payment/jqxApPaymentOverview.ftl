<div class="tab-pane active" id="payment-overview">	
	<h3 style="text-align:center;font-weight:bold; padding:0;line-height:20px">${StringUtil.wrapString(uiLabelMap.DAPayment)}</h3>
	<div class="form-horizontal basic-custom-form form-size-mini form-decrease-padding">
		<div class="row margin_left_10 row-desc">
			<div class="span6">
				<div class="control-group no-left-margin">
					<label class="control-label-desc">${uiLabelMap.FormFieldTitle_paymentTypeId}:</label>  
					<div class="controls-desc">
						<span >
							<b><i id="paymentTypeIdLabel"></i></b>
						</span>
					</div>
				</div>
				<div class="control-group no-left-margin">
					<label class="control-label-desc">${uiLabelMap.FormFieldTitle_paymentMethodTypeId}:</label>  
					<div class="controls-desc">
						<span>
							<b><i id="paymentMethodTypeIdLabel"></i></b>
						</span>
					</div>
				</div>
				<div class="control-group no-left-margin">
					<label class="control-label-desc">${uiLabelMap.CommonStatus}:</label>  
					<div class="controls-desc">
						<span>
							<b><i id="statusIdLabel"></i></b>
						</span>
					</div>
				</div>
				<div class="control-group no-left-margin">
					<label class="control-label-desc">${uiLabelMap.FormFieldTitle_paymentMethodId}:</label>  
					<div class="controls-desc">
						<span>
							<b><i id="paymentMethodIdLabel"></i></b>
						</span>
					</div>
				</div>
				<div class="control-group no-left-margin">
					<label class="control-label-desc">${uiLabelMap.accAccountingFromParty}:</label>  
					<div class="controls-desc">
						<span>
							<b><i id="partyIdFromLabel"></i></b>
						</span>
					</div>
				</div>
				<div class="control-group no-left-margin">
					<label class="control-label-desc">${uiLabelMap.accAccountingToParty}:</label>  
					<div class="controls-desc">
						<span>
							<b><i id="partyIdToLabel"></i></b>
						</span>
					</div>
				</div>
				<div class="control-group no-left-margin">
					<label class="control-label-desc">${uiLabelMap.FormFieldTitle_paymentRefNum}:</label>  
					<div class="controls-desc">
						<span>
							<b><i>${payment.paymentRefNum?if_exists}</i></b>
						</span>
					</div>
				</div>
			</div>
			<div class="span6">
				<div class="control-group no-left-margin">
					<label class="control-label-desc">${uiLabelMap.FormFieldTitle_paymentPreferenceId}:</label>  
					<div class="controls-desc">
						<span>
							<b><i>${payment.paymentPreferenceId?if_exists}</i></b>
						</span>
					</div>
				</div>
				<div class="control-group no-left-margin">
					<label class="control-label-desc">${uiLabelMap.FormFieldTitle_amount}:</label>  
					<div class="controls-desc">
						<span>
							<b><i id="amountLabel"></i></b>
						</span>
					</div>
				</div>
				<div class="control-group no-left-margin">
					<label class="control-label-desc">${uiLabelMap.AccountingActualCurrencyAmount}:</label>  
					<div class="controls-desc">
						<span>
							<b><i>${payment.actualCurrencyAmount?if_exists}</i></b>
						</span>
					</div>
				</div>
				<div class="control-group no-left-margin">
					<label class="control-label-desc">${uiLabelMap.FormFieldTitle_effectiveDate}:</label>  
					<div class="controls-desc">
						<span>
							<b><i id="effectiveDateLabel"></i></b>
						</span>
					</div>
				</div>
				<div class="control-group no-left-margin">
					<label class="control-label-desc">${uiLabelMap.FormFieldTitle_comments}:</label>  
					<div class="controls-desc">
						<span>
							<b><i>${payment.comments?if_exists}</i></b>
						</span>
					</div>
				</div>
				<div class="control-group no-left-margin">
					<label class="control-label-desc">${uiLabelMap.FormFieldTitle_overrideGlAccountId}:</label>  
					<div class="controls-desc">
						<span>
							<b><i>${payment.overrideGlAccountId?if_exists}</i></b>
						</span>
					</div>
				</div>
				<div class="control-group no-left-margin">
					<label class="control-label-desc">${uiLabelMap.FormFieldTitle_paymentGatewayResponseId}:</label>  
					<div class="controls-desc">
						<span>
							<b><i>${payment.paymentGatewayResponseId?if_exists}</i></b>
						</span>
					</div>
				</div>
			</div>
		</div>
	</div>
	<#include "component://delys/webapp/delys/accounting/editPaymentApplicationsInvJQ.ftl" />
	<#include "jqxApPaymentTrans.ftl" />
</div>
<!--================================Init data JS =============================================-->


<script>
	var dataPaymentType = [
			<#list listPaymentType as item>
				{
					<#assign description = StringUtil.wrapString(item.get("description", locale)) />
					"paymentTypeId" : "${StringUtil.wrapString(item.paymentTypeId?if_exists)}",
					"description" : "${description}"					
				},
			</#list>
		];
	for(var i = 0; i < dataPaymentType.length; i++){
		if(dataPaymentType[i].paymentTypeId == '${payment.paymentTypeId?if_exists}'){
			$("#paymentTypeIdLabel").html(dataPaymentType[i].description);
			break;
		}
	}
	var paymentMethodData = [
		<#list listPaymentMethod as item>
			{
				<#assign description = StringUtil.wrapString(item.get("description", locale)) />
				"paymentMethodId" : "${StringUtil.wrapString(item.paymentMethodId?if_exists)}",
				"description" : "${description}"					
			},
		</#list>
	];
   	for(var i = 0; i < paymentMethodData.length; i++){
   		if(paymentMethodData[i].paymentMethodId == '${payment.paymentMethodId?if_exists}'){
   			$("#paymentMethodIdLabel").html(paymentMethodData[i].description);
   			break;
   		}
   	}
   	var paymentMethodTypeData = [
		<#list listPaymentMethodType as item>
			{
				<#assign description = StringUtil.wrapString(item.get("description", locale)) />
				"paymentMethodTypeId" : "${StringUtil.wrapString(item.paymentMethodTypeId?if_exists)}",
				"description" : "${description}"					
			},
		</#list>
	];
	for(var i = 0; i < paymentMethodTypeData.length; i++){
		if(paymentMethodTypeData[i].paymentMethodTypeId == '${payment.paymentMethodTypeId?if_exists}'){
			$("#paymentMethodTypeIdLabel").html(paymentMethodTypeData[i].description);
			break;
		}
	}
	var statusData = [
 		<#list listStatus as item>
 			{
 				<#assign description = StringUtil.wrapString(item.get("description", locale)) />
 				"statusId" : "${StringUtil.wrapString(item.statusId?if_exists)}",
 				"description" : "${description}"					
 			},
 		</#list>
 	];
 	for(var i = 0; i < statusData.length; i++){
 		if(statusData[i].statusId == '${payment.statusId?if_exists}'){
 			$("#statusIdLabel").html(statusData[i].description);
 			break;
 		}
 	}
 	$.ajax({
		url: 'getPartyName',
		type: 'POST',
		data: {partyId: '${payment.partyIdFrom?if_exists}'},
		dataType: 'json',
		async: false,
		success : function(data) {
			if(!data._ERROR_MESSAGE_){
				var partyName = data.partyName;
				$("#partyIdFromLabel").html(partyName);
			}
        }
	});
 	$.ajax({
		url: 'getPartyName',
		type: 'POST',
		data: {partyId: '${payment.partyIdTo?if_exists}'},
		dataType: 'json',
		async: false,
		success : function(data) {
			if(!data._ERROR_MESSAGE_){
				var partyName = data.partyName;
				$("#partyIdToLabel").html(partyName);
			}
        }
	});
 	<#assign amount = Static["org.ofbiz.base.util.UtilFormatOut"].formatCurrency(payment.amount?if_exists, payment.currencyUomId?if_exists, locale)>
 	<#assign effectiveDate = Static["org.ofbiz.base.util.UtilFormatOut"].formatDateTime(payment.effectiveDate?if_exists, "dd/MM/yyyy - HH:mm:ss", locale, timeZone) />
 	var amount = "${amount}";
    var effectiveDate = "${effectiveDate}";
    $("#amountLabel").html(amount);
    $("#effectiveDateLabel").html(effectiveDate);
</script>
	<!--================================/End Init data JS =============================================-->