<#assign orderCancelReason = delegator.findByAnd("Enumeration", {"enumTypeId" : "ORDER_CANCEL_CODE"}, null, false)!/>
<@jqOlbCoreLib hasCore=false hasValidator=true/>
<script type="text/javascript">
	var reasonCancel = [
		{	enumId: "",
			descriptionSearch: "${StringUtil.wrapString(uiLabelMap.OtherReason)}"
		},
	<#if orderCancelReason?exists>
		<#list orderCancelReason as reasonItem>
		{	enumId: "${reasonItem.enumId}",
			descriptionSearch: "[${reasonItem.enumCode?if_exists}] ${StringUtil.wrapString(reasonItem.get("description", locale))}"
		},
		</#list>
	</#if>
	];
	if (uiLabelMap == undefined) var uiLabelMap = {};
	uiLabelMap.OtherReason = "${StringUtil.wrapString(uiLabelMap.OtherReason)}";
	uiLabelMap.validFieldRequire = "${StringUtil.wrapString(uiLabelMap.validFieldRequire)}";
</script>
<script type="text/javascript" src="/salesresources/js/order/orderViewDetailOpen.js"></script>
<div id="confirmOrderChangeStatus" style="display:none">
	<div>${uiLabelMap.BSCancelOrder}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<div id="containerOrderChangeStatus" style="background-color: transparent; overflow: auto;"></div>
		    <div id="jqxNotificationOrderChangeStatus" style="margin-bottom:5px">
		        <div id="notificationOrderChangeStatus">
		        </div>
		    </div>
			<div class="row-fluid">
				<div class="span12 form-window-content-custom">
					<div class='row-fluid'>
						<div class='span3'>
							<label for="wcos_changeReason" class="required">${uiLabelMap.BSReasonCancel}</label>
						</div>
						<div class='span9'>
							<div id="wcos_changeReason"></div>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span3'>
							<label for="wcos_changeDescription" class="required">${uiLabelMap.OtherReason}</label>
						</div>
						<div class='span9'>
							<textarea id="wcos_changeDescription" style="width: 95%"></textarea>
				   		</div>
					</div>
				</div>
			</div>
		</div>
	   	<div class="form-action">
	   		<div class="pull-right form-window-content-custom">
	   			<button id="alterConfirmSave" class='btn btn-primary form-action-button'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
				<button id="alterConfirmCancel" class='btn btn-danger form-action-button'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
	   		</div>
		</div>
	</div>
</div>