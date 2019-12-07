<div class="row-fluid">
	<div class="span12">
		<div id="notification"></div>
		<div id="container" style="width : 100%"></div>
		<div class="widget-box transparent" id="recent-box">
			<div class="widget-header" style="border-bottom:none">
				<div style="width:100%; border-bottom: 1px solid #c5d0dc">
					<div class="row-fluid">
						<div class="span10">
							<ul class="nav nav-tabs" id="recent-tab">
								<#if payment.statusId != 'PMNT_CANCELLED' && payment.statusId != 'PMNT_CONFIRMED' && payment.statusId != 'PMNT_VOID'>
									<li class="active">
										<a data-toggle="tab" href="#payment-overview" aria-expanded="true">
											<i class="fa fa-money"></i>
											${uiLabelMap.paymentOverview}
										</a>
									</li>
									<li class="">
										<a data-toggle="tab" href="#payment-header" aria-expanded="false">
											<i class="fa fa-header"></i>
											${uiLabelMap.paymentHeader}
										</a>
									</li>
									<li class="">
										<a data-toggle="tab" href="#payment-appl" aria-expanded="false">
											<i class="fa fa-hand-o-right"></i>
											${uiLabelMap.paymentAppl}
										</a>
									</li>
								<#else>
									<li class="active">
										<a data-toggle="tab" href="#payment-overview" aria-expanded="true">
											<i class="fa fa-money"></i>
											${uiLabelMap.paymentOverview}
										</a>
									</li>
								</#if>
							</ul>
						</div>
						<div class="span2" style="height:34px; text-align:right">
							<#if payment.statusId == 'PMNT_NOT_PAID'>
								<a href="javascript:document.PaymentSent.submit()" data-rel="tooltip" title="" data-placement="bottom" class="button-action" data-original-title="${StringUtil.wrapString(uiLabelMap.CommonSend)}"><i class="fa-hand-o-right"></i></a>
							  	<form name="PaymentSent" method="post" action="accApsetPaymentStatus" style="position:absolute;">
							        <input type="hidden" name="statusId" value="PMNT_SENT">
							        <input type="hidden" name="paymentId" value="${parameters.paymentId}">
							  	</form>
							  	
							  	<a href="javascript:document.PaymentCancel.submit()" data-rel="tooltip" title="" data-placement="bottom" class="button-action" data-original-title="${StringUtil.wrapString(uiLabelMap.CommonCancel)}"><i class="fa fa-ban"></i></a>
						      	<form name="PaymentCancel" method="post" action="accApsetPaymentStatus" style="position:absolute;">
							      	<input type="hidden" name="statusId" value="PMNT_CANCELLED">
							        <input type="hidden" name="paymentId" value="${parameters.paymentId}">
						      	</form>
						      	
						      	<a href="javascript:document.VoidPayment.submit()" data-rel="tooltip" title="" data-placement="bottom" class="button-action" data-original-title="Void"><i class="fa fa-circle-thin"></i></a>
						      	<form name="VoidPayment" method="post" action="accApvoidPayment" style="position:absolute;">
							        <input type="hidden" name="paymentId" value="${parameters.paymentId}">
						      	</form>
							<#elseif payment.statusId == 'PMNT_SENT'>
								<a href="javascript:document.PaymentConfirm.submit()" data-rel="tooltip" title="" data-placement="bottom" class="button-action" data-original-title="${StringUtil.wrapString(uiLabelMap.ManufacturingConfirmProductionRun)}"><i class="fa fa-check-circle"></i></a>
						      	<form name="PaymentConfirm" method="post" action="accApsetPaymentStatus" style="position:absolute;">
							      	<input type="hidden" name="statusId" value="PMNT_CONFIRMED">
							        <input type="hidden" name="paymentId" value="${parameters.paymentId}">
						      	</form>
						      	<a href="javascript:document.VoidPayment.submit()" data-rel="tooltip" title="" data-placement="bottom" class="button-action" data-original-title="Void"><i class="fa fa-circle-thin"></i></a>
						      	<form name="VoidPayment" method="post" action="accApvoidPayment" style="position:absolute;">
							        <input type="hidden" name="paymentId" value="${parameters.paymentId}">
						      	</form>
							<#elseif payment.statusId == 'PMNT_CANCELLED'>
								<a href="javascript:document.VoidPayment.submit()" data-rel="tooltip" title="" data-placement="bottom" class="button-action" data-original-title="Void"><i class="fa fa-circle-thin"></i></a>
						      	<form name="VoidPayment" method="post" action="accApvoidPayment" style="position:absolute;">
							        <input type="hidden" name="paymentId" value="${parameters.paymentId}">
						      	</form>
							</#if>
						</div>
					</div>
				</div>
			</div>
			<div class="widget-body" style="margin-top: -12px !important">
				<div class="widget-main padding-4">
					<div class="tab-content">
						<#include "jqxApPaymentOverview.ftl">
						<#include "component://delys/webapp/delys/accounting/formJqxAccounting/jqEditPayment.ftl">
						<#include "jqxApPaymentAppl.ftl">
					</div>
				</div>
			</div>
		</div>
	</div>
</div>
<style type="text/css">
	.button-action {
		font-size:18px; padding:0 0 0 8px;
	}
</style>
<script type="text/javascript">
	$('[data-rel=tooltip]').tooltip();
</script>