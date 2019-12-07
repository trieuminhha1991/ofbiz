<@jqGridMinimumLib />
<script type="text/javascript" src="/accresources/js/acc.bootbox.js"></script>
<#include "script/paymentViewScript.ftl" />

<div id="containerjqxgrid" class="container-noti"><#-- style="background-color: transparent; overflow: auto;"-->
</div>
<div id="jqxNotificationjqxgrid">
    <div id="notificationContentjqxgrid">
    </div>
</div>

<div id="container_payment_header" style="width :100%;padding:0;margin: 0"></div>
<div class="row-fluid">
	<div class="span12">
		<div class="widget-box transparent" id="recent-box">
			<div class="widget-header" style="border-bottom:none">
				<div style="width:100%; border-bottom: 1px solid #c5d0dc">
					<div class="row-fluid">
						<div class="span10">
							<ul class="nav nav-tabs" id="recent-tab">
								<#if security.hasPermission("PMN_POVER_VIEW", session) || security.hasPermission("PMN_SOVER_VIEW", session)>
									<li class="active">
										<a data-toggle="tab" href="#payment-overview" aria-expanded="true">
											${uiLabelMap.BACCPaymentOverview}
										</a>
									</li>
								</#if>
								<#if security.hasPermission("PMN_PTRANS_VIEW", session) || security.hasPermission("PMN_STRANS_VIEW", session)>								
									<li class="">
										<a data-toggle="tab" href="#payment-transaction" aria-expanded="false">
											${uiLabelMap.BACCTransaction}
										</a>
									</li>
								</#if>
								<#if security.hasPermission("PMN_PAPLPM_VIEW", session) || security.hasPermission("PMN_SAPLPM_VIEW", session)>								
									<li class="">
										<a data-toggle="tab" href="#payment-appl" aria-expanded="false">
											${uiLabelMap.BACCPaymentAppl}
										</a>
									</li>
								</#if>
							</ul>
						<div id="jqxNtf">
							<div id="jqxNtf_Content"></div>
						</div>
						</div>
						<div class="span2" style="height:34px; text-align:right; font-size: 20px;">
						<#if businessType == 'AP'>
							<#assign pdfUrl = "payment_voucher.pdf"/>
							<#if payment.paymentMethodTypeId == "CASH">
								<#assign pdfTitle = StringUtil.wrapString(uiLabelMap.BACCExpenditure)/>
							<#else>
								<#assign pdfTitle = StringUtil.wrapString(uiLabelMap.accPaymentOrderPrint)/>
							</#if>
						<#else>
							<#assign pdfUrl = "receipt_voucher.pdf"/>
							<#assign pdfTitle = StringUtil.wrapString(uiLabelMap.BACCReceipts)/>
						</#if>
						<a href="javascript:document.PaymentVoucher.submit()" data-rel="tooltip" title="" data-placement="bottom" class="button-action" 
							data-original-title="${pdfTitle}"><i class="fa fa-file-pdf-o"></i></a>
						<form name="PaymentVoucher" method="get" target="_blank" action="${pdfUrl}" style="position:absolute;">
					        <input type="hidden" name="paymentId" value="${parameters.paymentId}">
					  	</form>
						<#if businessType == 'AP'>
							<#if payment.statusId == 'PMNT_NOT_PAID'>
								<#if security.hasPermission("PMN_POVER_EDIT", session)>		
									<#assign isPaymentEditable = true/>
									<!-- <a href="javascript:accbootbox.confirmAct('send','${StringUtil.wrapString(uiLabelMap.BACCSent)}','PaymentSent')" data-rel="tooltip" title="" data-placement="bottom" class="button-action" data-original-title="${uiLabelMap.BACCSent}"><i class="fa fa-gift"></i></a> -->
									<a href="javascript:paymentReceiveOrSentObj.setPaymentReceiveOrSent('${parameters.paymentId}','${businessType}', '${StringUtil.wrapString(uiLabelMap.BACCSent)}')" 
										data-rel="tooltip" title="" data-placement="bottom" class="button-action" data-original-title="${uiLabelMap.BACCSent}">
										<i class="fa fa-gift"></i></a>
								  	<form name="PaymentSent" method="post" action="setAPPaymentStatus" style="position:absolute;">
								        <input type="hidden" name="statusId" value="PMNT_SENT">
								        <input type="hidden" name="paymentId" value="${parameters.paymentId}">
								    </form>
								</#if>
								  	
							  	<#--><a href="javascript:paymentViewObj.setPaymentStatus('PaymentCancel', '${StringUtil.wrapString(uiLabelMap.BACCCancelPaymentConfirm)}')" 
							  		data-rel="tooltip" title="" data-placement="bottom" class="button-action" data-original-title="${uiLabelMap.BACCCancel}"><i class="fa fa-times"></i></a>
						      	<form name="PaymentCancel" method="post" action="setAPPaymentStatus" style="position:absolute;">
							      	<input type="hidden" name="statusId" value="PMNT_CANCELLED">
							        <input type="hidden" name="paymentId" value="${parameters.paymentId}">
						      	</form>-->
						      	<a href="javascript:cancelPaymentTrans.openWindow1()" data-rel="tooltip" title="" data-placement="bottom" class="button-action" data-original-title="${uiLabelMap.BACCCancel}"><i class="fa fa-times"></i></a>
						      	
						      	<#--<a href="javascript:accbootbox.confirmAct('void','${StringUtil.wrapString(uiLabelMap.BACCVoid)}','VoidPayment')" data-rel="tooltip" title="" data-placement="bottom" class="button-action" data-original-title="${uiLabelMap.BACCVoid}"><i class="fa fa-circle-thin"></i></a>
						      	<form name="VoidPayment" method="post" action="<@ofbizUrl>voidAPPayment</@ofbizUrl>" style="position:absolute;">
							        <input type="hidden" name="paymentId" value="${parameters.paymentId}">
						      	</form>-->
						      	<a href="javascript:voidPaymentTrans.openWindow()" data-rel="tooltip" title="" data-placement="bottom" class="button-action" data-original-title="${uiLabelMap.BACCVoid}"><i class="fa fa-circle-thin"></i></a>
							<#elseif payment.statusId == 'PMNT_SENT'>
								<a href="javascript:paymentViewObj.setPaymentStatus('PaymentConfirm', '${StringUtil.wrapString(uiLabelMap.BACCConfirmPaymentConfirm)}')" 
									data-rel="tooltip" title="" data-placement="bottom" class="button-action" data-original-title="${uiLabelMap.BACCConfirmed}"><i class="fa fa-check-circle"></i></a>
						      	<form name="PaymentConfirm" method="post" action="setAPPaymentStatus" style="position:absolute;">
							      	<input type="hidden" name="statusId" value="PMNT_CONFIRMED">
							        <input type="hidden" name="paymentId" value="${parameters.paymentId}">
						      	</form>
						      	
						      	<#--<a href="javascript:accbootbox.confirmAct('void','${StringUtil.wrapString(uiLabelMap.BACCVoid)}','VoidPayment')" data-rel="tooltip" title="" data-placement="bottom" class="button-action" data-original-title="${uiLabelMap.BACCVoid}"><i class="fa fa-circle-thin"></i></a>
						      	<form name="VoidPayment" method="post" action="<@ofbizUrl>voidAPPayment</@ofbizUrl>" style="position:absolute;">
							        <input type="hidden" name="paymentId" value="${parameters.paymentId}">
						      	</form>-->
								<a href="javascript:voidPaymentTrans.openWindow()" data-rel="tooltip" title="" data-placement="bottom" class="button-action" data-original-title="${uiLabelMap.BACCVoid}"><i class="fa fa-circle-thin"></i></a>
							<#elseif payment.statusId == 'PMNT_CANCELLED'>
								<#--<a href="javascript:accbootbox.confirmAct('void','${StringUtil.wrapString(uiLabelMap.BACCVoid)}','VoidPayment')" data-rel="tooltip" title="" data-placement="bottom" class="button-action" data-original-title="${uiLabelMap.BACCVoid}"><i class="fa fa-circle-thin"></i></a>
						      	<form name="VoidPayment" method="post" action="<@ofbizUrl>voidAPPayment</@ofbizUrl>" style="position:absolute;">
							        <input type="hidden" name="paymentId" value="${parameters.paymentId}">
						      	</form>-->
								<a href="javascript:voidPaymentTrans.openWindow()" data-rel="tooltip" title="" data-placement="bottom" class="button-action" data-original-title="${uiLabelMap.BACCVoid}"><i class="fa fa-circle-thin"></i></a>
							</#if>
						<#else>
							<#if payment.statusId == 'PMNT_NOT_PAID'>
								<#if security.hasPermission("PMN_SOVER_EDIT", session)>	
									<#assign isPaymentEditable = true/>	
									<!-- <a id="PaymentReceive" href="javascript:accbootbox.confirmAct('receive','${StringUtil.wrapString(uiLabelMap.BACCReceived)}','PaymentReceive')" data-rel="tooltip" title="" data-placement="bottom" class="button-action" data-original-title="${uiLabelMap.BACCReceived}"><i class="fa fa-gift"></i></a> -->
									<a href="javascript:paymentReceiveOrSentObj.setPaymentReceiveOrSent('${parameters.paymentId}','${businessType}', '${StringUtil.wrapString(uiLabelMap.BACCReceived)}')" 
										data-rel="tooltip" title="" data-placement="bottom" class="button-action" data-original-title="${uiLabelMap.BACCReceived}"><i class="fa fa-gift"></i></a>
								  	<form name="PaymentReceive" method="post" action="setARPaymentStatus" style="position:absolute;">
								        <input type="hidden" name="statusId" value="PMNT_RECEIVED">
								        <input type="hidden" name="paymentId" value="${parameters.paymentId}">
								  	</form>
								</#if>
							  	
							  	<#--<a href="javascript:paymentViewObj.setPaymentStatus('PaymentCancel', '${StringUtil.wrapString(uiLabelMap.BACCCancelPaymentConfirm)}')" 
							  		data-rel="tooltip" title="" data-placement="bottom" class="button-action" data-original-title="${uiLabelMap.BACCCancel}"><i class="fa fa-times"></i></a>
						      	<form name="PaymentCancel" method="post" action="setARPaymentStatus" style="position:absolute;">
							      	<input type="hidden" name="statusId" value="PMNT_CANCELLED">
							        <input type="hidden" name="paymentId" value="${parameters.paymentId}">
						      	</form>-->
						      	<a href="javascript:cancelPaymentTrans.openWindow1()" data-rel="tooltip" title="" data-placement="bottom" class="button-action" data-original-title="${uiLabelMap.BACCCancel}"><i class="fa fa-times"></i></a>
						      	
						      	<#--<a href="javascript:accbootbox.confirmAct('void','${StringUtil.wrapString(uiLabelMap.BACCVoid)}','VoidPayment')" data-rel="tooltip" title="" data-placement="bottom" class="button-action" data-original-title="${uiLabelMap.BACCVoid}"><i class="fa fa-circle-thin"></i></a>
						      	<form name="VoidPayment" method="post" action="<@ofbizUrl>voidARPayment</@ofbizUrl>" style="position:absolute;">
							        <input type="hidden" name="paymentId" value="${parameters.paymentId}">
						      	</form>-->
								<a href="javascript:voidPaymentTrans.openWindow()" data-rel="tooltip" title="" data-placement="bottom" class="button-action" data-original-title="${uiLabelMap.BACCVoid}"><i class="fa fa-circle-thin"></i></a>
							<#elseif payment.statusId == 'PMNT_RECEIVED'>
								<a href="javascript:paymentViewObj.setPaymentStatus('PaymentConfirm', '${StringUtil.wrapString(uiLabelMap.BACCConfirmPaymentConfirm)}')" 
									data-rel="tooltip" title="" data-placement="bottom" class="button-action" data-original-title="${uiLabelMap.BACCConfirmed}"><i class="fa fa-check-circle"></i></a>
						      	<form name="PaymentConfirm" method="post" action="setARPaymentStatus" style="position:absolute;">
							      	<input type="hidden" name="statusId" value="PMNT_CONFIRMED">
							        <input type="hidden" name="paymentId" value="${parameters.paymentId}">
						      	</form>
						      	
						      	<#--<a href="javascript:accbootbox.confirmAct('void','${StringUtil.wrapString(uiLabelMap.BACCVoid)}','VoidPayment')" data-rel="tooltip" title="" data-placement="bottom" class="button-action" data-original-title="${uiLabelMap.BACCVoid}"><i class="fa fa-circle-thin"></i></a>
						      	<form name="VoidPayment" method="post" action="<@ofbizUrl>voidARPayment</@ofbizUrl>" style="position:absolute;">
							        <input type="hidden" name="paymentId" value="${parameters.paymentId}">
						      	</form>-->
								<a href="javascript:voidPaymentTrans.openWindow()" data-rel="tooltip" title="" data-placement="bottom" class="button-action" data-original-title="${uiLabelMap.BACCVoid}"><i class="fa fa-circle-thin"></i></a>
							<#elseif payment.statusId == 'PMNT_CANCELLED'>
								<#--<a href="javascript:accbootbox.confirmAct('void','${StringUtil.wrapString(uiLabelMap.BACCVoid)}','VoidPayment')" data-rel="tooltip" title="" data-placement="bottom" class="button-action" data-original-title="${uiLabelMap.BACCVoid}"><i class="fa fa-circle-thin"></i></a>
						      	<form name="VoidPayment" method="post" action="<@ofbizUrl>voidARPayment</@ofbizUrl>" style="position:absolute;">
							        <input type="hidden" name="paymentId" value="${parameters.paymentId}">
						      	</form>-->
								<a href="javascript:voidPaymentTrans.openWindow()" data-rel="tooltip" title="" data-placement="bottom" class="button-action" data-original-title="${uiLabelMap.BACCVoid}"><i class="fa fa-circle-thin"></i></a>
							</#if>
						</#if>
						</div>
					</div>
				</div>
			</div>
			<div class="widget-body" style="margin-top: -12px !important">
				<div class="widget-main padding-4">
					<div id="notification" class="container-noti"></div>
					<div class="tab-content overflow-visible" style="padding:8px 0">
						<div id="payment-overview" class="tab-pane active">
							<#--<!-- <#include "paymentViewDetail.ftl"> -->
							<#include "paymentViewOverview.ftl"/>
						</div>
						<div id="payment-transaction" class="tab-pane">
							<#include "paymentViewTrans.ftl"/>
						</div>
						<div id="payment-appl" class="tab-pane">
							<#include "paymentViewAppl.ftl"/>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>
<script type="text/javascript" src="/accresources/js/payment/paymentView.js"></script>
<#if isPaymentEditable?exists && isPaymentEditable>
	<div id="quickEditPaymMethodWindow" class="hide">
		<div>${uiLabelMap.BACCPleaseChooseAcc}...</div>
		<div class='form-window-container' style="position: relative;">
			<div class='form-window-content'>
				<div class='row-fluid'>
					<div class="span5 text-algin-right">
						<label class="asterisk">${StringUtil.wrapString(uiLabelMap.BACCPaymentMethodTypeId)}</label>
					</div>
					<div class="span7">
						<div id="quickPaymentMethodList"></div>
					</div>
				</div>
			</div>
			<div class="form-action">
				<button type="button" class='btn btn-danger form-action-button pull-right'id="cancelQuickEditPaymentMethod">
					<i class='icon-remove'></i>&nbsp;${uiLabelMap.CommonCancel}</button>	
				<button type="button" class='btn btn-primary form-action-button pull-right' id="saveQuickEditPaymentMethod">
					<i class='fa fa-check'></i>&nbsp;${uiLabelMap.BACCOK}</button>
			</div>
		</div>	
	</div>
	<script type="text/javascript" src="/accresources/js/payment/paymentReceiveOrSent.js?v=20170318"></script>
	<#include "paymentEdit.ftl"/>
</#if>

<div id="cancelPaymentTrans" class='hide'>
	<div>${uiLabelMap.BACCCancelPaymentTransactionTitle}</div>
	<div class='form-window-container'>
		<form id='formCancelPayment'>
			<div class='form-window-content'>
				<div class="row-fluid">
					<div class="span12">
						<div class='row-fluid margin-bottom10'>
							<div class='span5 text-algin-right'>
								<label class='required'>${uiLabelMap.BACCPaymentId}</label>
							</div>
							<div class='span7'>
								<input id ='paymentCodeTrans'/>
								<input type="hidden" id ='paymentIdTrans'/>
							</div>
						</div>
						
						<div class='row-fluid margin-bottom10'>
							<div class='span5 text-algin-right'>
								<label class='required'>${uiLabelMap.BACCCancelPaymentTransactionDate}</label>
							</div>
							<div class='span7'>
								<div id="dateCancelPaymentInput"></div>
							</div>
						</div>
					</div>
				</div>
			</div>
			<div class="form-action">
 				<button type="button" class='btn btn-danger form-action-button pull-right'id="alterCancel">
 					<i class='icon-remove'></i>&nbsp;${uiLabelMap.CommonCancel}</button>	
 				<button type="button" class='btn btn-primary form-action-button pull-right' id="alterSave">
 					<i class='fa fa-check'></i>&nbsp;${uiLabelMap.CommonSave}</button>
 			</div>
 			<input type="hidden" id='statusIdTrans' name="statusIdTrans" value="PMNT_CANCELLED"/>
		</form>
	</div>
</div>

<div id="voidPaymentTrans" class='hide'>
	<div>${uiLabelMap.BACCVoidTitle}</div>
	<div class='form-window-container'>
		<form id='formVoidPayment'>
			<div class='form-window-content'>
				<div class="row-fluid">
					<div class="span12">
						<div class='row-fluid margin-bottom10'>
							<div class='span5 text-algin-right'>
								<label class='required'>${uiLabelMap.BACCPaymentId}</label>
							</div>
							<div class='span7'>
								<input id ='paymentCodeTransVoid'/>
								<input type="hidden" id ='paymentIdTransVoid'/>
							</div>
						</div>
						
						<div class='row-fluid margin-bottom10'>
							<div class='span5 text-algin-right'>
								<label class='required'>${uiLabelMap.BACCVoidPaymentTransactionDate}</label>
							</div>
							<div class='span7'>
								<div id="dateVoidPaymentInput"></div>
							</div>
						</div>
					</div>
				</div>
			</div>
			<div class="form-action">
 				<button type="button" class='btn btn-danger form-action-button pull-right'id="alterCancelVoid">
 					<i class='icon-remove'></i>&nbsp;${uiLabelMap.CommonCancel}</button>	
 				<button type="button" class='btn btn-primary form-action-button pull-right' id="alterSaveVoid">
 					<i class='fa fa-check'></i>&nbsp;${uiLabelMap.CommonSave}</button>
 			</div>
		</form>
	</div>
</div>

<script type="text/javascript">
	$('[data-rel=tooltip]').tooltip();
	accbootbox.getLabels({ok : '${StringUtil.wrapString(uiLabelMap.wgok)}',
						cancel : '${StringUtil.wrapString(uiLabelMap.wgcancel)}',
						header1 : '${StringUtil.wrapString(uiLabelMap.BACCConfirms)}',
						header2 : '${StringUtil.wrapString(uiLabelMap.BACCThisPayment)}'});
</script>

<script>
	var OLBPayView = function(){
	};
	
	OLBPayView.prototype = {
		bindEvent : function(){
			var parent = this;
		}
	}
	$(document).on('ready', function(){
		cancelPaymentTrans.init1();
		voidPaymentTrans.init();
		var olbPayView = new OLBPayView();
		olbPayView.bindEvent();
	});
	
	var cancelPaymentTrans = (function(){
		var init1 = function(){
			initInput1();
			createJqxWindow1();
			initEvent1();
			initValidator1();
		};
		
		var createJqxWindow1 = function(){
			var height ='180';
			var width = '400';
			var divEl = $("#cancelPaymentTrans");
			accutils.createJqxWindow(divEl,width, height);
		};
		
		var openWindow1 = function(){
			var divEl = $("#cancelPaymentTrans");
			accutils.openJqxWindow(divEl);
		};
		
		var initInput1 = function(){
			$("#paymentCodeTrans").jqxInput({width: '94%', height: 22, disabled:true});
			$("#dateCancelPaymentInput").jqxDateTimeInput({width: '96%', height: 25});
		};
		var initOpen1 = function(){
			$("#paymentIdTrans").val("${parameters.paymentId}");
			$("#paymentCodeTrans").jqxInput('val',"${payment.paymentCode?if_exists}");
		};
		var initValidator1 = function(){
			$('#formCancelPayment').jqxValidator({
		        rules: [
		        	{input: '#paymentCodeTrans', message: uiLabelMap.FieldRequired, action: 'keyup, change, close', 
		       				rule: function (input, commit) {
		                       if(input.val()){
		                    	   return true;
		                       }else{
		                    	   return false;
		                       }
		    				}
	    			},
	    			{input: '#dateCancelPaymentInput', message: uiLabelMap.FieldRequired, action: 'keyup, change, close', 
		    				rule: function (input, commit) {
		                       if(input.val()){
		                    	   return true;
		                       }
		                       return false;
		    				}
	    			},
	        	]});
		};
		
		var cancelPaymentTrans = function(){
			var submitdata ={};
			var busiUrl="";
			submitdata['paymentId'] = $("#paymentIdTrans").val();
			var dateCancelPaymentInput = $("#dateCancelPaymentInput").jqxDateTimeInput('getDate');
			submitdata['transactionDate'] = accutils.getTimestamp(dateCancelPaymentInput);
			submitdata['statusId'] = $("#statusIdTrans").val();
			Loading.show('loadingMacro');
			<#if businessType == 'AP'>
				busiUrl = "setAPPaymentStatus";
			<#else>
				busiUrl = "setARPaymentStatus";
			</#if>
			$.ajax({
				url: busiUrl,
				type: "POST",
				data: submitdata,
				success: function(data) {
					<#if businessType == "AR">
						window.location.href = 'ViewARPayment?paymentId=${parameters.paymentId}';
					<#else>
						window.location.href = 'ViewAPPayment?paymentId=${parameters.paymentId}';
					</#if>	
				},
		  	});
		};
		initEvent1 = function(){
			$('#alterCancel').on('click', function(){
				$("#cancelPaymentTrans").jqxWindow('close');
			});
			
			$('#alterSave').on('click', function(){
				var valid = $('#formCancelPayment').jqxValidator('validate');
				if(!valid){
					return;
				}
				bootbox.dialog(uiLabelMap.BACCCancelPaymentConfirm,
					[{
						"label" : uiLabelMap.CommonSubmit,
						"class" : "btn-primary btn-small icon-ok open-sans",
						"callback": function() {
							cancelPaymentTrans();
						}
					 },
					 {
						 "label" : uiLabelMap.CommonCancel,
						 "class" : "btn-danger btn-small icon-remove open-sans",
					 }]		
				);
			});
			
			$("#cancelPaymentTrans").on('open', function(event){
				initOpen1();
			});
			
			$("#cancelPaymentTrans").on('close', function(event){
				$('#paymentIdTrans').val("");
				$('#paymentCodeTrans').val("");
				$("#dateCancelPaymentInput").val("");
				$("#dateCancelPaymentInput").jqxDateTimeInput('val', new Date());
				$('#formCancelPayment').jqxValidator('hide');
			});
		};
		
		return {
			init1: init1,
			openWindow1: openWindow1
		}
	}());
	
	var voidPaymentTrans = (function(){
		var init = function(){
			initInput();
			createJqxWindow();
			initEvent();
			initValidator();
		};
		
		var createJqxWindow = function(){
			var height ='180';
			var width = '400';
			var divEl = $("#voidPaymentTrans");
			accutils.createJqxWindow(divEl,width, height);
		};
		
		var openWindow = function(){
			var divEl = $("#voidPaymentTrans");
			accutils.openJqxWindow(divEl);
		};
		
		var initInput = function(){
			$("#paymentCodeTransVoid").jqxInput({width: '94%', height: 22, disabled:true});
			$("#dateVoidPaymentInput").jqxDateTimeInput({width: '96%', height: 25});
		};
		var initOpen = function(){
			$("#paymentIdTransVoid").val("${parameters.paymentId}");
			$("#paymentCodeTransVoid").jqxInput('val',"${payment.paymentCode?if_exists}");
		};
		var initValidator = function(){
			$('#formVoidPayment').jqxValidator({
		        rules: [
		        	{input: '#paymentCodeTransVoid', message: uiLabelMap.FieldRequired, action: 'keyup, change, close', 
		       				rule: function (input, commit) {
		                       if(input.val()){
		                    	   return true;
		                       }else{
		                    	   return false;
		                       }
		    				}
	    			},
	    			{input: '#dateVoidPaymentInput', message: uiLabelMap.FieldRequired, action: 'keyup, change, close', 
		    				rule: function (input, commit) {
		                       if(input.val()){
		                    	   return true;
		                       }
		                       return false;
		    				}
	    			},
	        	]});
		};
		
		var voidPaymentTrans = function(){
			var submitdata ={};
			var busiUrl="";
			submitdata['paymentId'] = $("#paymentIdTransVoid").val();			
			var dateVoidPaymentInput = $("#dateVoidPaymentInput").jqxDateTimeInput('getDate');
			submitdata['transactionDate'] = accutils.getTimestamp(dateVoidPaymentInput);
			Loading.show('loadingMacro');
			<#if businessType == 'AP'>
				busiUrl = "voidAPPayment";
			<#else>
				busiUrl = "voidARPayment";
			</#if>
			$.ajax({
				url: busiUrl,
				type: "POST",
				data: submitdata,
				success: function(data) {
					<#if businessType == "AR">
						window.location.href = 'ViewARPayment?paymentId=${parameters.paymentId}';
					<#else>
						window.location.href = 'ViewAPPayment?paymentId=${parameters.paymentId}';
					</#if>	
				},
		  	});
		};
		initEvent = function(){
			$('#alterCancelVoid').on('click', function(){
				$("#voidPaymentTrans").jqxWindow('close');
			});
			
			$('#alterSaveVoid').on('click', function(){
				var valid = $('#formVoidPayment').jqxValidator('validate');
				if(!valid){
					return;
				}
				bootbox.dialog(uiLabelMap.BACCVoidPaymentConfirm,
					[{
						"label" : uiLabelMap.CommonSubmit,
						"class" : "btn-primary btn-small icon-ok open-sans",
						"callback": function() {
							voidPaymentTrans();
						}
					 },
					 {
						 "label" : uiLabelMap.CommonCancel,
						 "class" : "btn-danger btn-small icon-remove open-sans",
					 }]		
				);
			});
			
			$("#voidPaymentTrans").on('open', function(event){
				initOpen();
			});
			
			$("#voidPaymentTrans").on('close', function(event){
				$('#paymentIdTransVoid').val("");
				$('#paymentCodeTransVoid').val("");
				$("#dateVoidPaymentInput").val("");
				$("#dateVoidPaymentInput").jqxDateTimeInput('val', new Date());
				$('#formVoidPayment').jqxValidator('hide');
			});
		};
		
		return {
			init: init,
			openWindow: openWindow
		}
	}());
</script>	