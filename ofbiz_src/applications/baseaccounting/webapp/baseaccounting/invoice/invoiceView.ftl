<#include "script/invoiceViewScript.ftl"/>
<div id="containerjqxgrid" class="container-noti"><#-- style="background-color: transparent; overflow: auto;"-->
</div>
<div id="jqxNotificationjqxgrid">
    <div id="notificationContentjqxgrid">
    </div>
</div>
<div id="" style="width :100%;padding:0;margin: 0;"></div>
<div class="row-fluid">
	<div class="span12">
		<div class="widget-box transparent" id="recent-box">
			<div class="widget-header" style="border-bottom:none">
				<div style="width:100%; border-bottom: 1px solid #c5d0dc">
					<div class="row-fluid">
						<div class="span10">
							<#if parameters.active?exists>
								<#assign ativeTab = parameters.active/> 
							<#else>
								<#assign ativeTab = "invoice-overview"/>
							</#if>
							<ul class="nav nav-tabs" id="recent-tab">
								<#if security.hasPermission("INV_POVER_VIEW", session) || security.hasPermission("INV_SOVER_VIEW", session)>
									<li class='<#if ativeTab == "invoice-overview">active</#if>'>
										<a data-toggle="tab" href="#invoice-overview" aria-expanded="true">
											${uiLabelMap.BACCInvoiceOverview}
										</a>
									</li>
								</#if>
								<#if security.hasPermission("INV_PTRANS_VIEW", session) || security.hasPermission("INV_STRANS_VIEW", session)>
									<li class='<#if ativeTab == "invoice-transaction">active</#if>'>
										<a data-toggle="tab" href="#invoice-transaction" aria-expanded="false">
											${uiLabelMap.BACCTransaction}
										</a>
									</li>
								</#if>
								<#if security.hasPermission("INV_PAPLPM_VIEW", session) || security.hasPermission("INV_SAPLPM_VIEW", session)>
									<li class='<#if ativeTab == "invoice-appl">active</#if>'>
										<a data-toggle="tab" href="#invoice-appl" aria-expanded="false">
											${uiLabelMap.BACCPayment}
										</a>
									</li>
								</#if>
								<#if invoice.statusId != "INVOICE_IN_PROCESS">
									<li class='<#if ativeTab == "voucher-appl">active</#if>'>
										<a data-toggle="tab" href="#voucher-appl" aria-expanded="false">
											${uiLabelMap.CommonVoucher}
										</a>
									</li>
								</#if>
                                <#if businessType == 'AR'>
                                    <li class='' id="invoiceSpecificationTab">
                                        <a data-toggle="tab" href="#bangKe" aria-expanded="false">
                                        ${uiLabelMap.BangKe}
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
							  	<#if invoice.statusId == "INVOICE_IN_PROCESS">
								  	<#--<!-- <a href="javascript:accbootbox.confirmAct('approve','${StringUtil.wrapString(uiLabelMap.BACCApprove)}','ApprovedInvoice')" data-rel="tooltip" title="" data-placement="bottom" class="button-action" data-original-title="${uiLabelMap.BACCApprove}"><i class="fa fa-check-circle"></i></a>
								  	<form name="ApprovedInvoice" method="post" action="<@ofbizUrl>setAPInvoiceStatus</@ofbizUrl>" style="position:absolute;">
								        <input type="hidden" name="invoiceId" value="${parameters.invoiceId}">
								        <input type="hidden" name="statusId" value="INVOICE_APPROVED">
								  	</form> -->
								  	<#if security.hasPermission("INV_POVER_EDIT", session)>																	
									  	<#--<!-- <a href="javascript:accbootbox.confirmAct('receive','${StringUtil.wrapString(uiLabelMap.BACCReceived)}','ReceivedInvoice')" data-rel="tooltip" title="" data-placement="bottom" class="button-action" data-original-title="${uiLabelMap.BACCReceived}"><i class="fa fa-arrow-circle-o-down"></i></a>
									  	<form name="ReceivedInvoice" method="post" action="<@ofbizUrl>setAPInvoiceStatus</@ofbizUrl>" style="position:absolute;">
									        <input type="hidden" name="invoiceId" value="${parameters.invoiceId}">
									        <input type="hidden" name="statusId" value="INVOICE_RECEIVED">
									  	</form> -->
									</#if>
								  	<a href="javascript:invoice.setInvoiceReady(${parameters.invoiceId}, '${uiLabelMap.BACCReady}')" data-rel="tooltip" title="" data-placement="bottom" class="button-action" data-original-title="${uiLabelMap.BACCReady}"><i class="fa fa-play"></i></a>
								  	<form name="ReadyInvoice" method="post" action="<@ofbizUrl>setAPInvoiceStatus</@ofbizUrl>" style="position:absolute;">
								        <input type="hidden" name="invoiceId" value="${parameters.invoiceId}">
								        <input type="hidden" name="statusId" value="INVOICE_READY">
								  	</form>
								  	<a href="javascript:accbootbox.confirmAct('cancel','${StringUtil.wrapString(uiLabelMap.BACCCancel)}','CancelInvoice')" data-rel="tooltip" title="" data-placement="bottom" class="button-action" data-original-title="${uiLabelMap.BACCCancel}"><i class="fa fa-times"></i></a>
								  	<form name="CancelInvoice" method="post" action="<@ofbizUrl>setAPInvoiceStatus</@ofbizUrl>" style="position:absolute;">
								        <input type="hidden" name="invoiceId" value="${parameters.invoiceId}">
								        <input type="hidden" name="statusId" value="INVOICE_CANCELLED">
								  	</form>
								<#elseif invoice.statusId == "INVOICE_READY">
									<#--<!-- <a href="javascript:accbootbox.confirmAct('writeoff','${StringUtil.wrapString(uiLabelMap.BACCWriteOff)}','WriteOffInvoice')" data-rel="tooltip" title="" data-placement="bottom" class="button-action" data-original-title="<#if businessType == 'AR'>${uiLabelMap.BACCWriteOff}<#else>${uiLabelMap.BACCWriteOffPurchase}</#if>"><i class="fa fa-pause"></i></a>
								  	<form name="WriteOffInvoice" method="post" action="<@ofbizUrl>setAPInvoiceStatus</@ofbizUrl>" style="position:absolute;">
								        <input type="hidden" name="invoiceId" value="${parameters.invoiceId}">
								        <input type="hidden" name="statusId" value="INVOICE_WRITEOFF">
								  	</form> -->
								  	<#--<!-- <a href="javascript:accbootbox.confirmAct('paid','${StringUtil.wrapString(uiLabelMap.BACCPaid)}','PaidInvoice')" data-rel="tooltip" title="" data-placement="bottom" class="button-action" data-original-title="${uiLabelMap.BACCPaid}"><i class="fa fa-caret-square-o-right"></i></a>
								  	<form name="PaidInvoice" method="post" action="<@ofbizUrl>setAPInvoiceStatus</@ofbizUrl>" style="position:absolute;">
								        <input type="hidden" name="invoiceId" value="${parameters.invoiceId}">
								        <input type="hidden" name="statusId" value="INVOICE_PAID">
								  	</form> -->
								  	<a href="javascript:cancelInvoiceTrans.openWindow()" data-rel="tooltip" title="" data-placement="bottom" class="button-action" data-original-title="${uiLabelMap.BACCCancel}"><i class="fa fa-times"></i></a>
								<#elseif invoice.statusId == "INVOICE_APPROVED">
									<#--<!-- <a href="javascript:accbootbox.confirmAct('received','${StringUtil.wrapString(uiLabelMap.BACCReceived)}','ReceivedInvoice')" data-rel="tooltip" title="" data-placement="bottom" class="button-action" data-original-title="${uiLabelMap.BACCReceived}"><i class="fa fa-arrow-circle-o-down"></i></a>
								  	<form name="ReceivedInvoice" method="post" action="<@ofbizUrl>setAPInvoiceStatus</@ofbizUrl>" style="position:absolute;">
								        <input type="hidden" name="invoiceId" value="${parameters.invoiceId}">
								        <input type="hidden" name="statusId" value="INVOICE_RECEIVED">
								  	</form> -->
								  	<a href="javascript:invoice.setInvoiceReady(${parameters.invoiceId}, '${uiLabelMap.BACCReady}')" data-rel="tooltip" title="" data-placement="bottom" class="button-action" data-original-title="${uiLabelMap.BACCReady}"><i class="fa fa-play"></i></a>
								  	<form name="ReadyInvoice" method="post" action="<@ofbizUrl>setAPInvoiceStatus</@ofbizUrl>" style="position:absolute;">
								        <input type="hidden" name="invoiceId" value="${parameters.invoiceId}">
								        <input type="hidden" name="statusId" value="INVOICE_READY">
								  	</form>
								<#elseif invoice.statusId == "INVOICE_RECEIVED">
									<#--<!-- <a href="javascript:accbootbox.confirmAct('approve','${StringUtil.wrapString(uiLabelMap.BACCApprove)}','ApprovedInvoice')" data-rel="tooltip" title="" data-placement="bottom" class="button-action" data-original-title="${uiLabelMap.BACCApprove}"><i class="fa fa-check-circle"></i></a>
								  	<form name="ApprovedInvoice" method="post" action="<@ofbizUrl>setAPInvoiceStatus</@ofbizUrl>" style="position:absolute;">
								        <input type="hidden" name="invoiceId" value="${parameters.invoiceId}">
								        <input type="hidden" name="statusId" value="INVOICE_APPROVED">
								  	</form> -->
								  	<#--<!-- <a href="javascript:accbootbox.confirmAct('inprocess','${StringUtil.wrapString(uiLabelMap.BACCInProcessing)}','InProcessInvoice')" data-rel="tooltip" title="" data-placement="bottom" class="button-action" data-original-title="${uiLabelMap.BACCInProcessing}"><i class="fa fa-spinner"></i></a>
								  	<form name="InProcessInvoice" method="post" action="<@ofbizUrl>setAPInvoiceStatus</@ofbizUrl>" style="position:absolute;">
								        <input type="hidden" name="invoiceId" value="${parameters.invoiceId}">
								        <input type="hidden" name="statusId" value="INVOICE_IN_PROCESS">
								  	</form> -->
								  	<a href="javascript:invoice.setInvoiceReady(${parameters.invoiceId}, '${uiLabelMap.BACCReady}')" data-rel="tooltip" title="" data-placement="bottom" class="button-action" data-original-title="${uiLabelMap.BACCReady}"><i class="fa fa-play"></i></a>
								  	<form name="ReadyInvoice" method="post" action="<@ofbizUrl>setAPInvoiceStatus</@ofbizUrl>" style="position:absolute;">
								        <input type="hidden" name="invoiceId" value="${parameters.invoiceId}">
								        <input type="hidden" name="statusId" value="INVOICE_READY">
								  	</form>
								  	<a href="javascript:accbootbox.confirmAct('cancel','${StringUtil.wrapString(uiLabelMap.BACCCancel)}','CancelInvoice')" data-rel="tooltip" title="" data-placement="bottom" class="button-action" data-original-title="${uiLabelMap.BACCCancel}"><i class="fa fa-times"></i></a>
								  	<form name="CancelInvoice" method="post" action="<@ofbizUrl>setAPInvoiceStatus</@ofbizUrl>" style="position:absolute;">
								        <input type="hidden" name="invoiceId" value="${parameters.invoiceId}">
								        <input type="hidden" name="statusId" value="INVOICE_CANCELLED">
								  	</form>
								</#if>
						  	<#else>
								<a href="javascript:document.InvoicePDF.submit()" data-rel="tooltip" title="" data-placement="bottom" class="button-action" data-original-title="PDF"><i class="fa fa-file-pdf-o"></i></a>
							  	<form name="InvoicePDF" method="post" target="_blank" action="<@ofbizUrl>invoice_fill_in.pdf</@ofbizUrl>" style="position:absolute;">
							        <input type="hidden" name="invoiceId" value="${parameters.invoiceId}">
							  	</form>
								<#if invoice.statusId == "INVOICE_IN_PROCESS">
									<#if security.hasPermission("INV_SOVER_EDIT", session)>	
										<#--<!-- <a href="javascript:accbootbox.confirmAct('approve','${StringUtil.wrapString(uiLabelMap.BACCApprove)}','ApprovedInvoice')" data-rel="tooltip" title="" data-placement="bottom" class="button-action" data-original-title="${uiLabelMap.BACCApprove}"><i class="fa fa-check-circle"></i></a>
									  	<form name="ApprovedInvoice" method="post" action="<@ofbizUrl>setARInvoiceStatus</@ofbizUrl>" style="position:absolute;">
									        <input type="hidden" name="invoiceId" value="${parameters.invoiceId}">
									        <input type="hidden" name="statusId" value="INVOICE_APPROVED">
									  	</form> -->
									</#if>
								  	<#--<!-- <a href="javascript:accbootbox.confirmAct('sent','${StringUtil.wrapString(uiLabelMap.BACCSent)}','SentInvoice')" data-rel="tooltip" title="" data-placement="bottom" class="button-action" data-original-title="${uiLabelMap.BACCSent}"><i class="fa fa-paper-plane"></i></a>
								  	<form name="SentInvoice" method="post" action="<@ofbizUrl>setARInvoiceStatus</@ofbizUrl>" style="position:absolute;">
								        <input type="hidden" name="invoiceId" value="${parameters.invoiceId}">
								        <input type="hidden" name="statusId" value="INVOICE_SENT">
								  	</form> -->
								  	<a href="javascript:accbootbox.confirmAct('ready','${StringUtil.wrapString(uiLabelMap.BACCReady)}','ReadyInvoice')" data-rel="tooltip" title="" data-placement="bottom" class="button-action" data-original-title="${uiLabelMap.BACCReady}"><i class="fa fa-play"></i></a>
								  	<form name="ReadyInvoice" method="post" action="<@ofbizUrl>setARInvoiceStatus</@ofbizUrl>" style="position:absolute;">
								        <input type="hidden" name="invoiceId" value="${parameters.invoiceId}">
								        <input type="hidden" name="statusId" value="INVOICE_READY">
								  	</form>
								  	<a href="javascript:accbootbox.confirmAct('cancel','${StringUtil.wrapString(uiLabelMap.BACCCancel)}','CancelInvoice')" data-rel="tooltip" title="" data-placement="bottom" class="button-action" data-original-title="${uiLabelMap.BACCCancel}"><i class="fa fa-times"></i></a>
								  	<form name="CancelInvoice" method="post" action="<@ofbizUrl>setARInvoiceStatus</@ofbizUrl>" style="position:absolute;">
								        <input type="hidden" name="invoiceId" value="${parameters.invoiceId}">
								        <input type="hidden" name="statusId" value="INVOICE_CANCELLED">
								  	</form>
								<#elseif invoice.statusId == "INVOICE_READY">
									<#--<!-- <a href="javascript:accbootbox.confirmAct('writeoff','${StringUtil.wrapString(uiLabelMap.BACCWriteOff)}','WriteOffInvoice')" data-rel="tooltip" title="" data-placement="bottom" class="button-action" data-original-title="${uiLabelMap.BACCWriteOff}"><i class="fa fa-pause"></i></a>
								  	<form name="WriteOffInvoice" method="post" action="<@ofbizUrl>setARInvoiceStatus</@ofbizUrl>" style="position:absolute;">
								        <input type="hidden" name="invoiceId" value="${parameters.invoiceId}">
								        <input type="hidden" name="statusId" value="INVOICE_WRITEOFF">
								  	</form> -->
								  	<#--<!-- <a href="javascript:accbootbox.confirmAct('paid','${StringUtil.wrapString(uiLabelMap.BACCPaid)}','PaidInvoice')" data-rel="tooltip" title="" data-placement="bottom" class="button-action" data-original-title="${uiLabelMap.BACCPaid}"><i class="fa fa-caret-square-o-right"></i></a>
								  	<form name="PaidInvoice" method="post" action="<@ofbizUrl>setARInvoiceStatus</@ofbizUrl>" style="position:absolute;">
								        <input type="hidden" name="invoiceId" value="${parameters.invoiceId}">
								        <input type="hidden" name="statusId" value="INVOICE_PAID">
								  	</form> -->
								  	<a href="javascript:cancelInvoiceTrans.openWindow()" data-rel="tooltip" title="" data-placement="bottom" class="button-action" data-original-title="${uiLabelMap.BACCCancel}"><i class="fa fa-times"></i></a>
								<#elseif invoice.statusId == "INVOICE_APPROVED">
									<#--<!-- <a href="javascript:accbootbox.confirmAct('sent','${StringUtil.wrapString(uiLabelMap.BACCSent)}','SentInvoice')" data-rel="tooltip" title="" data-placement="bottom" class="button-action" data-original-title="${uiLabelMap.BACCSent}"><i class="fa fa-paper-plane"></i></a>
								  	<form name="SentInvoice" method="post" action="<@ofbizUrl>setARInvoiceStatus</@ofbizUrl>" style="position:absolute;">
								        <input type="hidden" name="invoiceId" value="${parameters.invoiceId}">
								        <input type="hidden" name="statusId" value="INVOICE_SENT">
								  	</form> -->
								  	<a href="javascript:accbootbox.confirmAct('ready','${StringUtil.wrapString(uiLabelMap.BACCReady)}','ReadyInvoice')" data-rel="tooltip" title="" data-placement="bottom" class="button-action" data-original-title="${uiLabelMap.BACCReady}"><i class="fa fa-play"></i></a>
								  	<form name="ReadyInvoice" method="post" action="<@ofbizUrl>setARInvoiceStatus</@ofbizUrl>" style="position:absolute;">
								        <input type="hidden" name="invoiceId" value="${parameters.invoiceId}">
								        <input type="hidden" name="statusId" value="INVOICE_READY">
								  	</form>
								<#elseif invoice.statusId == "INVOICE_SENT">
									<#--<!-- <a href="javascript:accbootbox.confirmAct('approve','${StringUtil.wrapString(uiLabelMap.BACCApprove)}','ApprovedInvoice')" data-rel="tooltip" title="" data-placement="bottom" class="button-action" data-original-title="${uiLabelMap.BACCApprove}"><i class="fa fa-check-circle"></i></a>
								  	<form name="ApprovedInvoice" method="post" action="<@ofbizUrl>setARInvoiceStatus</@ofbizUrl>" style="position:absolute;">
								        <input type="hidden" name="invoiceId" value="${parameters.invoiceId}">
								        <input type="hidden" name="statusId" value="INVOICE_APPROVED">
								  	</form> -->
								  	<#--<!-- <a href="javascript:accbootbox.confirmAct('inprocess','${StringUtil.wrapString(uiLabelMap.BACCInProcessing)}','InProcessInvoice')" data-rel="tooltip" title="" data-placement="bottom" class="button-action" data-original-title="${uiLabelMap.BACCInProcessing}"><i class="fa fa-spinner"></i></a>
								  	<form name="InProcessInvoice" method="post" action="<@ofbizUrl>setARInvoiceStatus</@ofbizUrl>" style="position:absolute;">
								        <input type="hidden" name="invoiceId" value="${parameters.invoiceId}">
								        <input type="hidden" name="statusId" value="INVOICE_IN_PROCESS">
								  	</form> -->
								  	<a href="javascript:accbootbox.confirmAct('ready','${StringUtil.wrapString(uiLabelMap.BACCReady)}','ReadyInvoice')" data-rel="tooltip" title="" data-placement="bottom" class="button-action" data-original-title="${uiLabelMap.BACCReady}"><i class="fa fa-play"></i></a>
								  	<form name="ReadyInvoice" method="post" action="<@ofbizUrl>setARInvoiceStatus</@ofbizUrl>" style="position:absolute;">
								        <input type="hidden" name="invoiceId" value="${parameters.invoiceId}">
								        <input type="hidden" name="statusId" value="INVOICE_READY">
								  	</form>
								  	<a href="javascript:accbootbox.confirmAct('cancel','${StringUtil.wrapString(uiLabelMap.BACCCancel)}','CancelInvoice')" data-rel="tooltip" title="" data-placement="bottom" class="button-action" data-original-title="${uiLabelMap.BACCCancel}"><i class="fa fa-times"></i></a>
								  	<form name="CancelInvoice" method="post" action="<@ofbizUrl>setARInvoiceStatus</@ofbizUrl>" style="position:absolute;">
								        <input type="hidden" name="invoiceId" value="${parameters.invoiceId}">
								        <input type="hidden" name="statusId" value="INVOICE_CANCELLED">
								  	</form>
								</#if>
						  	</#if>
						</div>
					</div>
				</div>
			</div>
			<div class="widget-body" style="margin-top: -12px !important">
				<div class="widget-main padding-4">
					<div id="notification">
					</div>
					<div id="container" style="width: 100%;"></div>
					<div class="tab-content overflow-visible" style="padding:8px 0;">
						<div class='tab-pane <#if ativeTab == "invoice-overview">active</#if>' id="invoice-overview">
							<#include "invoiceViewDetail.ftl">
						</div>
						<div id='invoice-transaction' class='tab-pane <#if ativeTab == "invoice-transaction">active</#if>'>
							<#include "invoiceViewTrans.ftl"/>
						</div>
						<div id='invoice-appl' class='tab-pane <#if ativeTab == "invoice-appl">active</#if>'>
							<#include "invoiceViewAppl.ftl"/>
						</div>
						<#if invoice.statusId != "INVOICE_IN_PROCESS">
							<div id='voucher-appl' class='tab-pane <#if ativeTab == "voucher-appl">active</#if>'>
								<#include "voucherInvoiceView.ftl"/>
							</div>
						</#if>
						<div id="bangKe" class='tab-pane'>
							<div id="InvoiceSpecContent"></div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>

<div id="cancelInvoiceTrans" class='hide'>
	<div>${uiLabelMap.BaccCancelInvoiceTransactionTitle}</div>
	<div class='form-window-container'>
		<form id='formCancelInvoice'>
			<div class='form-window-content'>
				<div class="row-fluid">
					<div class="span12">
						<div class='row-fluid margin-bottom10'>
							<div class='span5 text-algin-right'>
								<label class='required'>${uiLabelMap.BACCInvoiceId}</label>
							</div>
							<div class='span7'>
								<input id ='invoiceIdTrans'/>
							</div>
						</div>
						
						<div class='row-fluid margin-bottom10'>
							<div class='span5 text-algin-right'>
								<label class='required'>${uiLabelMap.BaccCancelInvoiceTransactionDate}</label>
							</div>
							<div class='span7'>
								<div id="dateCancelInvoiceInput"></div>
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
 			<input type="hidden" id='statusIdTrans' name="statusIdTrans" value="INVOICE_CANCELLED"/>
		</form>
	</div>
</div>

<script type="text/javascript">
	$('[data-rel=tooltip]').tooltip();
	accbootbox.getLabels({ok : '${StringUtil.wrapString(uiLabelMap.wgok)}',
		cancel : '${StringUtil.wrapString(uiLabelMap.wgcancel)}',
		header1 : '${StringUtil.wrapString(uiLabelMap.BACCConfirms)}',
		header2 : '${StringUtil.wrapString(uiLabelMap.BACCThisInvoice)}'});
</script>
<script>
	var OLBInvView = function(){
	};
	
	OLBInvView.prototype = {
			bindEvent : function(){
				var parent = this;
			}
	}
	$(document).on('ready', function(){
		cancelInvoiceTrans.init();
		var olbInvView = new OLBInvView();
		olbInvView.bindEvent();
		
		//second tab
		var jqxGridInvAppl = new JQXGridInvAppl();
		jqxGridInvAppl.initGrid();
		var olbApply = new OLBApply();
		olbApply.initWindow();
		olbApply.bindEvent();
		
		//third tab
		var jqxGridTrans = new JQXGridTrans();
		jqxGridTrans.initGrid();
		var isLoadiInvSpec = false;
		$("#invoiceSpecificationTab").click(function(){
			if(!isLoadiInvSpec){
				Loading.show('loadingMacro');
				$.ajax({
					url: 'getInvoiceSpecificationView',
					data: {invoiceId: "${parameters.invoiceId}"},
					type: 'POST',
					success: function(response){
						isLoadiInvSpec = true;
						$("#InvoiceSpecContent").html(response);
					},
					complete: function(){
						Loading.hide('loadingMacro');
					}
				});
			}
		});
	});
	
	var cancelInvoiceTrans = (function(){
		var init = function(){
			initInput();
			createJqxWindow();
			initEvent();
			initValidator();
		};
		
		var createJqxWindow = function(){
			var height ='180';
			var width = '400';
			var divEl = $("#cancelInvoiceTrans");
			accutils.createJqxWindow(divEl,width, height);
		};
		
		var openWindow = function(){
			var divEl = $("#cancelInvoiceTrans");
			accutils.openJqxWindow(divEl);
		};
		
		var initInput = function(){
			$("#invoiceIdTrans").jqxInput({width: '94%', height: 22, disabled:true});
			$("#dateCancelInvoiceInput").jqxDateTimeInput({width: '96%', height: 25});
		};
		var initOpen = function(){
			$("#invoiceIdTrans").jqxInput('val',"${parameters.invoiceId}");
		};
		var initValidator = function(){
			$('#formCancelInvoice').jqxValidator({
		        rules: [
		        	{input: '#invoiceIdTrans', message: uiLabelMap.FieldRequired, action: 'keyup, change, close', 
		       				rule: function (input, commit) {
		                       if(input.val()){
		                    	   return true;
		                       }else{
		                    	   return false;
		                       }
		    				}
	    			},
	    			{input: '#dateCancelInvoiceInput', message: uiLabelMap.FieldRequired, action: 'keyup, change, close', 
		    				rule: function (input, commit) {
		                       if(input.val()){
		                    	   return true;
		                       }
		                       return false;
		    				}
	    			},
	        	]});
		};
		
		var cancelInvoiceTrans = function(){
			var submitdata ={};
			var busiUrl="";
			submitdata['invoiceId'] = $("#invoiceIdTrans").jqxInput('val');
			var dateCancelInvoiceInput = $("#dateCancelInvoiceInput").jqxDateTimeInput('getDate');
			submitdata['transactionDate'] = accutils.getTimestamp(dateCancelInvoiceInput);
			submitdata['statusId'] = $("#statusIdTrans").val();
			Loading.show('loadingMacro');
			<#if businessType == 'AP'>
				busiUrl = "setAPInvoiceStatus";
			<#else>
				busiUrl = "setARInvoiceStatus";
			</#if>
			$.ajax({
				url: busiUrl,
				type: "POST",
				data: submitdata,
				success: function(data) {
					<#if businessType == "AR">
						window.location.href = 'ViewARInvoice?invoiceId=${parameters.invoiceId}';
					<#else>
						window.location.href = 'ViewAPInvoice?invoiceId=${parameters.invoiceId}';
					</#if>	
				},
		  	});
		};
		initEvent = function(){
			$('#alterCancel').on('click', function(){
				$("#cancelInvoiceTrans").jqxWindow('close');
			});
			
			$('#alterSave').on('click', function(){
				var valid = $('#formCancelInvoice').jqxValidator('validate');
				if(!valid){
					return;
				}
				bootbox.dialog(uiLabelMap.BACCConfirmCancelInvoiceTransaction,
						[
						 {
							"label" : uiLabelMap.CommonSubmit,
							"class" : "btn-primary btn-small icon-ok open-sans",
							"callback": function() {
								cancelInvoiceTrans();
							}
						},
						 {
							 "label" : uiLabelMap.CommonCancel,
							 "class" : "btn-danger btn-small icon-remove open-sans",
						 }
						 ]		
				);
			});
			
			$("#cancelInvoiceTrans").on('open', function(event){
				initOpen();
			});
			
			$("#cancelInvoiceTrans").on('close', function(event){
				$('#invoiceIdTrans').val("");
				$("#dateCancelInvoiceInput").val("");
				$("#dateCancelInvoiceInput").jqxDateTimeInput('val', new Date());
				$('#formCancelInvoice').jqxValidator('hide');
			});
		};
		
		return {
			init: init,
			openWindow: openWindow
		}
	}());

    var invoice = (function(){
        var _invoiceId;
        var setInvoiceReady = function(invoiceId, actionDesc){
            _invoiceId = invoiceId;
            var warningMsg = '<div ><div class="row-fluid"><div class="span12" style="font-size : 13px;"><span style="color : #037c07;font-weight :bold;"><i class="fa-hand-o-right"></i>&nbsp;'
                    + uiLabelMap.accConfirms +'</span> <span style="color:red;font-weight : bold;">'
                    + actionDesc + '&nbsp; <span style="color : #037c07;font-weight :bold;">'
                    + uiLabelMap.accThisPayment  +'</span></span></div></div></div>';
            bootbox.dialog(warningMsg,
                    [{
                        "label" : uiLabelMap.CommonSubmit,
                        "class" : "btn-primary btn-small icon-ok open-sans",
                        "callback": function () {
                            checkImportTrade(invoiceId);
                        }
                    },
                        {
                            "label" : uiLabelMap.CommonCancel,
                            "class" : "btn-danger btn-small icon-remove open-sans"
                        }]
            );
        };
        var checkImportTrade = function(invoiceId){
            if('VND' !== globalVar.currencyUomId && OlbCore.isEmpty(globalVar.conversionFactor)) {
                bootbox.dialog(uiLabelMap.BACCPleaseInputConversionFactor,
                        [
                            {
                                "label" : uiLabelMap.CommonClose,
                                "class" : "btn-danger btn-small icon-remove open-sans",
                            }]
                );
            }
            else {
                setInvoiceStatus(invoiceId);
            }
        };
        var setInvoiceStatus = function(invoiceId){
                $("form[name='ReadyInvoice']").submit();
        };

        return{
            setInvoiceReady: setInvoiceReady
        }
    }());
</script>
