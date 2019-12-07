<@jqOlbCoreLib hasComboBox=true hasValidator=true/>
<script type="text/javascript">
	var reasonCancel = [<#if orderCancelReason?exists><#list orderCancelReason as reasonItem>{
		enumId: "${reasonItem.enumId}",
		descriptionSearch: "[${reasonItem.enumCode?if_exists}] ${StringUtil.wrapString(reasonItem.get("description", locale))}"
	},</#list></#if>];
	
	var actionName = "";
	$(function() {
		orderDetailPage.init();
	});
	var orderDetailPage = (function() {
		var init = function(){
			initElement();
			initEvent();
			initValidateForm();
		};
		var initElement = function() {
			$("#confirmOrderChangeStatus").jqxWindow({
				width: 540, height: 200, resizable: true, isModal: true, autoOpen: false, cancelButton: $("#alterConfirmCancel"), modalOpacity: 0.7, theme: theme
			});
			var configChangeReason = {
				placeHolder: "${StringUtil.wrapString(uiLabelMap.DAClickToChoose)}",
				key: "enumId",
				value: "descriptionSearch",
				width: "98%",
				dropDownHeight: 260,
				dropDownWidth: 500,
				autoDropDownHeight: false,
				displayDetail: true,
				dropDownHorizontalAlignment: "right",
				autoComplete: true,
				searchMode: "containsignorecase",
				renderer : null,
				renderSelectedItem : null
			};
			new OlbComboBox($("#wcos_changeReason"), reasonCancel, configChangeReason, []);
		};
		var initEvent = function() {
			$("#confirmOrderChangeStatus").on("open", function(event) {
				$("body").css("overflow", "hidden");
			});
			$("#confirmOrderChangeStatus").on("close", function(event) {
				$("body").css("overflow", "inherit");
			});
			$("#alterConfirmSave").on("click", function() {
				if ("CANCEL" == actionName) {
					if(!$("#confirmOrderChangeStatus").jqxValidator("validate")) return false;
					var resultInput = $("#wcos_changeReason").jqxComboBox("getSelectedItem");
					document.OrderCancel.changeReason.value = "" + resultInput.value;
					document.OrderCancel.submit();
				}
			});
			$("#confirmOrderChangeStatus").on("close", function(event) {
				$("#wcos_changeReason").jqxComboBox("close");
			});
		};
		var initValidateForm = function() {
			$("#confirmOrderChangeStatus").jqxValidator({
				position: "bottom",
				rules: [
					{input: "#wcos_changeReason", message: "${uiLabelMap.DARequired}", action: "change", 
						rule: function(input, commit){
							return OlbValidatorUtil.validElement(input, commit, "validInputNotNull");
						}
					}],
				scroll: false
			});
		};
		return {
			init: init
		}
	}());
	var changeOrderStatus = function(action) {
		if ("CANCEL" == action) {
			actionName = "CANCEL";
			$("#confirmOrderChangeStatus").jqxWindow("open");
		}
	};
	
	var checkAndDirectEdit = function(orderId) {
		var editable = "N";
		$.ajax({
			type : "POST",
			url : "checkOrderEditable",
			data : {
				orderId : orderId
			},
			success : function(data) {
				if (data._ERROR_MESSAGE_ || data._ERROR_MESSAGE_LIST_) {
					jOlbUtil.alert.error(data._ERROR_MESSAGE_);
				} else {
	        		editable = data.editable;
	        		if (editable == "Y") {
						window.location.href="editPurchaseOrder?orderId=" + orderId;		
					} else {
						jOlbUtil.alert.error("${uiLabelMap.POCannotChangeThisOrderHasPromoAndReceived}");
					}
				}
			},
		});
	};
</script>
<div id="confirmOrderChangeStatus" style="display:none">
	<div>${uiLabelMap.DACancelOrder}</div>
	<div class="form-window-container">
		<div class="form-window-content">
			<div id="containerOrderChangeStatus" style="background-color: transparent; overflow: auto;"></div>
			<div id="jqxNotificationOrderChangeStatus" style="margin-bottom:5px">
				<div id="notificationOrderChangeStatus">
				</div>
			</div>
			<div class="row-fluid">
				<div class="span12 form-window-content-custom">
					<div class="row-fluid">
						<div class="span3">
							<label for="wcos_changeReason" class="required">${uiLabelMap.DAReasonCancel}</label>
						</div>
						<div class="span9">
							<div id="wcos_changeReason"></div>
						</div>
					</div>
				</div>
			</div>
		</div>
		<div class="form-action">
			<div class="pull-right form-window-content-custom">
				<button id="alterConfirmSave" class="btn btn-primary form-action-button"><i class="fa-check"></i> ${uiLabelMap.CommonSave}</button>
				<button id="alterConfirmCancel" class="btn btn-danger form-action-button"><i class="fa-remove"></i> ${uiLabelMap.CommonCancel}</button>
			</div>
		</div>
	</div>
</div>