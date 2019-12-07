<@jqGridMinimumLib />
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxeditor.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<#assign mapFrom = dispatcher.runSync("getPartyEmail",{"userLogin",userLogin,"partyId","${invoice.partyIdFrom?if_exists}"})/>
<#assign mapTo = dispatcher.runSync("getPartyEmail",{"userLogin",userLogin,"partyId","${invoice.partyId?if_exists}"})/>
<div id="notification"></div>
<div id="sendPerlMail" class="widget-box transparent no-bottom-border">
	<div></div>
	<div id="container" style="width : 100%;"></div>
	<div class="widget-body">
		<div class="row-fluid" style="margin-left : -100px;">
			<div class="span12">
				<form name="sendEmailPerlForm" id="sendEmailPerlForm">	
					<div class="row-fluid space">
						<div class="span6 align-right asterisk">
							${uiLabelMap.FormFieldTitle_emailAddressFrom}
						</div>	
						<div class="span6">
							<input type="text" id="sendFrom"/>
						</div>
					</div>
					<div class="row-fluid space ">
						<div class="span6 align-right asterisk">
							${uiLabelMap.FormFieldTitle_emailAddressTo}
						</div>
						<div class="span6">
							<input type="text" id="sendTo"/>
						</div>
					</div>
					<div class="row-fluid space">
						<div class="span6 align-right">
							${uiLabelMap.FormFieldTitle_emailAddressCc}
						</div>
						<div class="span6">
							<input type="text" id="sendCc"/>
						</div>
					</div>	
					<div class="row-fluid space">
						<div class="span6 align-right">
							${uiLabelMap.FormFieldTitle_subject}
						</div>
						<div class="span6">
							<input type="text" id="subject"/>
						</div>
					</div>	
					<div class="row-fluid space">
						<div class="span6 align-right">
							${uiLabelMap.FormFieldTitle_otherCurrency}
						</div>
						<div class="span6" style="padding-left : 17px !important;">
							<div id="other"></div>
						</div>
					</div>	
					<div class="row-fluid">
						<div class="span6 align-right">
							${uiLabelMap.FormFieldTitle_bodyText}
						</div>
						<div class="span6">
							<textarea  id="bodyText"></textarea>
						</div>
					</div>
				</form>
				<div class="span12" style="margin-left : 1px;">
					<div class="row-fluid space">
							<div class="span6 align-right">
							</div>
							<div class="span6">
								<button id="sendEmail" class="btn btn-primary btn-small"><i class='fa-check'></i> ${uiLabelMap.CommonSubmit}</button>
							</div>
					</div>	
				</div>
			</div>
		</div>
	</div>
</div>			

<style type="text/css">
	.space{
		padding-bottom : 30px !important;
	}
</style>

<script type="text/javascript">
	$.jqx.theme = 'olbius';
	theme = $.jqx.theme;
	var actionSendEmail = (function(){
		var initElement = function(){
			<#if '${invoice.invoiceTypeId}' = 'SALES_INVOICE'>
				$('#sendFrom').jqxInput({width : '235px',height : '25px',value : '${StringUtil.wrapString(mapFrom.emailAddress?if_exists)}'});
				$('#sendTo').jqxInput({width : '235px',height : '25px',value : '${StringUtil.wrapString(mapTo.emailAddress?if_exists)}'});
			</#if>
			<#if '${invoice.invoiceTypeId}' = 'PURCHASE_INVOICE'>
				$('#sendFrom').jqxInput({width : '235px',height : '25px',value : '${StringUtil.wrapString(mapTo.emailAddress?if_exists)}'});
				$('#sendTo').jqxInput({width : '235px',height : '25px',value : '${StringUtil.wrapString(mapFrom.emailAddress?if_exists)}'});
			</#if>
			$('#sendCc').jqxInput({width : '225px',height : '25px'});
			$('#subject').jqxInput({width : '225px',height : '25px',value : 'Please find attached invoice.'});
			$('#other').jqxCheckBox({width : '300px',height : '25px',checked : true,theme :theme});
			$('#notification').jqxNotification({autoClose : true,autoCloseDelay : 2000,appendContainer : '#container',opacity : 1});
			$('#bodyText').jqxEditor({theme : theme,width : '500px',height : '150px',tools: 'bold italic underline | font size | left center right | outdent indent',
				createCommand: function (name) {
                    switch (name) {
                        case "font":
                            return {
                                init: function (widget) {
                                    widget.jqxDropDownList({
                                        source: [{ label: 'Arial', value: 'Arial, Helvetica, sans-serif' },
                                             { label: 'Comic Sans MS', value: '"Comic Sans MS", cursive, sans-serif' },
                                             { label: 'Courier New', value: '"Courier New", Courier, monospace' },
                                             { label: 'Georgia', value: "Georgia,serif" }]
                                    });
                                }
                            }
                        case "size":
                            return {
                                init: function (widget) {
                                    widget.jqxDropDownList({
                                        source: [
                                            { label: "8pt", value: "xx-small" },
                                            { label: "12pt", value: "small" },
                                            { label: "18pt", value: "large" },
                                            { label: "36pt", value: "xx-large" }
                                        ]
                                    });
                                }
                            }
                    }
                }
			});
		}
		
		var sendPerlEmail = function(){
			var row  = {};
			row = {
				invoiceId : '${parameters.invoiceId?if_exists}',
				sendFrom : $('#sendFrom').val(),
				sendTo : $('#sendTo').val(),
				sendCc : $('#sendCc').jqxInput('val'),
				subject : $('#subject').jqxInput('val'),
				other : $('#other').jqxCheckBox('val'),
				bodyText : $('#bodyText').jqxEditor('val')
			}
			submit(row);
		}
		
		var submit = function(row){
			if(!$('#sendEmailPerlForm').jqxValidator('validate')){return;}
			if(row){
				$.ajax({
					url  : 'accArexecuteSendPerEmailJS',
					datatype : 'JSON',
					type : 'POST',
					data : row,
					async : false,
					cache : false,
					success : function(response){
						if(response._ERROR_MESSAGE_LIST_){
								$('#notification').jqxNotification({template : 'error'});
								$('#notification').text(response._ERROR_MESSAGE_LIST_);
								$('#notification').jqxNotification('open');
						}else{
							$('#notification').jqxNotification({template : 'success'});
							$('#notification').text(response._EVENT_MESSAGE_);
							$('#notification').jqxNotification('open');
							clearForm();
						}	
					},
					error : function(){
					
					}
				});
			}
		}
		var clearForm = function(){
				$('#sendTo').jqxInput('val',null),
				$('#sendCc').jqxInput('val',null),
				$('#bodyText').jqxEditor('val','')
		}
		
		var initRules = function(){
			$('#sendEmailPerlForm').jqxValidator({
				rules : [
					{ input: '#sendFrom', message: '${StringUtil.wrapString(uiLabelMap.FieldRequiredAccounting?default(''))}', action: 'change, blur', rule: 'required' },
					{ input: '#sendTo', message: '${StringUtil.wrapString(uiLabelMap.FieldRequiredAccounting?default(''))}', action: 'change, blur', rule: 'required' }
				]
			})
		}
		
		var bindEvent = function(){
			$('#sendEmail').click(function(){
				if(!actionSendEmail.sendPerlEmail()){return;}else{
				    actionSendEmail.sendPerlEmail();}
			});
		}
		return 	{
					init : function(){
						initElement();
						bindEvent();
						initRules();
					},
					sendPerlEmail : sendPerlEmail
				}
	}());
		
	$(document).ready(function(){
		actionSendEmail.init();
	})
</script>