<div id="alterpopupWindowAgreementNew" style="display:none">
	<div>${uiLabelMap.accCreateNew}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<input type="hidden" id="wn_customerId" value=""/>
			<input type="hidden" id="wn_reg_productPromoId" value=""/>
			<input type="hidden" id="wn_reg_productPromoRuleId" value=""/>
			<input type="hidden" id="wn_reg_fromDate" value=""/>
			<div class="row-fluid">
				<div class="span12 form-window-content-custom">
					<div class='row-fluid'>
						<div class='span5'>
							<label>${uiLabelMap.BSCustomer}</label>
						</div>
						<div class='span7'>
							<div>
								<b><span id="wn_customerDesc" class="green"></span></b>
							</div>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label>${uiLabelMap.BSAgreementCode}</label>
						</div>
						<div class='span7'>
							<input id="wn_agreementCode"/>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label>${uiLabelMap.BSAgreementDate}</label>
						</div>
						<div class='span7'>
							<div id="wn_agreementDate"></div>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label>${uiLabelMap.BSFromDate}</label>
						</div>
						<div class='span7'>
							<div id="wn_fromDate"></div>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label>${uiLabelMap.BSThruDate}</label>
						</div>
						<div class='span7'>
							<div id="wn_thruDate"></div>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label>${uiLabelMap.BSDescription}</label>
						</div>
						<div class='span7'>
							<textarea id="wn_description" class="span12"></textarea>
				   		</div>
					</div>
				</div><!--.span12-->
			</div>
		</div>
	   	<div class="form-action">
	   		<div class="pull-right form-window-content-custom">
	   			<button id="wn_alterSave" class='btn btn-primary form-action-button'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
				<button id="wn_alterCancel" class='btn btn-danger form-action-button'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
	   		</div>
		</div>
	</div>
</div>

<div class="container_loader">
	<div id="loader_page_common" class="jqx-rc-all jqx-rc-all-olbius loader-page-common-custom">
		<div class="jqx-rc-all jqx-rc-all-olbius jqx-fill-state-normal jqx-fill-state-normal-olbius">
			<div>
				<div class="jqx-grid-load"></div>
				<span>${uiLabelMap.BSLoading}...</span>
			</div>
		</div>
	</div>
</div>

<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script type="text/javascript" src="/aceadmin/assets/js/fuelux/fuelux.wizard.min.js"></script>
<script type="text/javascript" src="/aceadmin/assets/js/bootbox.min.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtooltip.js"></script>
<@jqOlbCoreLib hasValidator=true/>
<script type="text/javascript">
	$(function(){
		OlbExhibitionAgreementNew.init();
	});
	var OlbExhibitionAgreementNew = (function(){
		var init = function() {
			initElement();
			initEvent();
		};
		var initElement = function(){
			jOlbUtil.input.create("#wn_agreementCode", {width: '96%'});
			jOlbUtil.dateTimeInput.create("#wn_agreementDate", {width: '99%', allowNullDate: true, value: new Date(), showFooter: true});
			jOlbUtil.dateTimeInput.create("#wn_fromDate", {width: '99%', allowNullDate: true, value: new Date(), showFooter: true});
			jOlbUtil.dateTimeInput.create("#wn_thruDate", {width: '99%', allowNullDate: true, value: null, showFooter: true});
			
			jOlbUtil.windowPopup.create($("#alterpopupWindowAgreementNew"), {width: 540, height: 390, cancelButton: $("#wn_alterCancel")});
		};
		var initEvent = function(){
			$("#wn_alterSave").click(function () {
				var dataMap = {
					customerId: $("#wn_customerId").val(),
					agreementCode: $("#wn_agreementCode").val(),
					description: $("#wn_description").val(),
					regProductPromoId: $("#wn_reg_productPromoId").val(),
					regProductPromoRuleId: $("#wn_reg_productPromoRuleId").val(),
					regFromDate: $("#wn_reg_fromDate").val(),
				};
				
				if (typeof($('#wn_agreementDate').jqxDateTimeInput('getDate')) != 'undefined' && $('#wn_agreementDate').jqxDateTimeInput('getDate') != null) {
					dataMap['agreementDate'] = $('#wn_agreementDate').jqxDateTimeInput('getDate').getTime();
				}
				if (typeof($('#wn_fromDate').jqxDateTimeInput('getDate')) != 'undefined' && $('#wn_fromDate').jqxDateTimeInput('getDate') != null) {
					dataMap['fromDate'] = $('#wn_fromDate').jqxDateTimeInput('getDate').getTime();
				}
				if (typeof($('#wn_thruDate').jqxDateTimeInput('getDate')) != 'undefined' && $('#wn_thruDate').jqxDateTimeInput('getDate') != null) {
					dataMap['thruDate'] = $('#wn_thruDate').jqxDateTimeInput('getDate').getTime();
				}
				
				$.ajax({
					type: 'POST',
					url: 'createAgreementFromPromoExtReg',
					data: dataMap,
					beforeSend: function(){
						$("#loader_page_common").show();
					},
					success: function(data){
						jOlbUtil.processResultDataAjax(data, function(data, errorMessage){
								        	$('#container').empty();
								        	$('#jqxNotification').jqxNotification({ template: 'error'});
								        	$("#jqxNotification").html(errorMessage);
								        	$("#jqxNotification").jqxNotification("open");
								        	return false;
										}, function(){
											$('#container').empty();
								        	$('#jqxNotification').jqxNotification({ template: 'info'});
								        	$("#jqxNotification").html("${uiLabelMap.wgupdatesuccess}");
								        	$("#jqxNotification").jqxNotification("open");
								        	
								        	$("#alterpopupWindowAgreementNew").jqxWindow('close');
								        	$("#${id}").jqxGrid("updatebounddata");
										}
								);
					},
					error: function(data){
						alert("Send request is error");
					},
					complete: function(data){
						$("#loader_page_common").hide();
					},
				});
		    });
		};
		var openWindowNewAgreement = function(){
			$("#alterpopupWindowAgreementNew").jqxWindow("open");
		};
		return {
			init: init,
			openWindowNewAgreement: openWindowNewAgreement,
		};
	}());
</script>
