<div id="alterpopupWindowQuick" style="display:none">
	<div>${uiLabelMap.BSQuickCreateNewCustomTimePeriod}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<div class="row-fluid">
				<div class="span12 form-window-content-custom">
					<div class='row-fluid'>
						<div class='span4'>
							<label for="wn_yearPeriod" class="required">${uiLabelMap.BSYear}</label>
						</div>
						<div class='span8'>
							<div id="wn_yearPeriod"></div>
				   		</div>
					</div>
				</div>
			</div>
		</div>
		<div class="form-action">
	   		<div class="pull-right form-window-content-custom">
	   			<button id="alterSaveQ" class='btn btn-primary form-action-button'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
				<button id="alterCancelQ" class='btn btn-danger form-action-button'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
	   		</div>
		</div>
	</div>
</div>

<script type="text/javascript">
	$(function(){
		OlbCTimePeriodQuickNew.init();
	});
	var OlbCTimePeriodQuickNew = (function(){
		var validatorVAL;
		
		var init = function(){
			initElement();
			initEvent();
			initValidateForm();
		};
		var initElement = function(){
			jOlbUtil.windowPopup.create($("#alterpopupWindowQuick"), {width: 560, height: 135, cancelButton: $("#alterCancelQ")});
			
			<#--
			$("#wn_yearPeriod").jqxNumberInput({width: '98%', height: 25, spinButtons: true, decimalDigits: 0, min: 1970, max: 9999, digits: 4, groupSeparator: '', promptChar: '#'});
			-->
			var nowTimestamp = new Date();
			var nowYear = nowTimestamp.getFullYear();
			var localDataYear = [];
			for (var i = (nowYear + 10); i > (nowYear - 10); i--) {
				localDataYear.push({'yearValue': i});
			}
			var configYearPeriod = {
				width: '95%',
				placeHolder: '${StringUtil.wrapString(uiLabelMap.BSClickToChoose)}',
				key: 'yearValue',
				value: 'yearValue',
				autoDropDownHeight: false,
				useUrl: false,
			};
			new OlbDropDownList($("#wn_yearPeriod"), localDataYear, configYearPeriod, [nowYear]);
		};
		var initEvent = function(){
		    $("#alterSaveQ").on('click', function(){
		    	if(!getValidator().validate()) return false;
		    	
		    	$.ajax({
					type: 'POST',
					url: 'createSalesCustomTimePeriodQuick',
					data: {yearPeriod: $('#wn_yearPeriod').val()},
					beforeSend: function(){
						$("#loader_page_common").show();
					},
					success: function(data){
						jOlbUtil.processResultDataAjax(data, "default", function(){
									$('#container').empty();
						        	$('#jqxNotification').jqxNotification({ template: 'info'});
						        	$("#jqxNotification").html("${StringUtil.wrapString(uiLabelMap.wgupdatesuccess)}");
						        	$("#jqxNotification").jqxNotification("open");
						        	
						        	$("#jqxCustomTimePeriod").jqxTreeGrid('updateBoundData');
						        	$("#alterpopupWindowQuick").jqxWindow('close');
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
		var initValidateForm = function(){
			var extendRules2 = [];
			var mapRules2 = [
				{input: "#wn_yearPeriod", type: "validCannotSpecialCharactor"},
			];
			validatorVAL = new OlbValidator($("#alterpopupWindowQuick"), mapRules2, extendRules2, {position: 'bottom'});
		};
		var getValidator = function(){
			return validatorVAL;
		};
		return {
			init: init
		}
	}());
</script>
<#--
<div class='row-fluid'>
	<div class='span5'>
		<label for="wn_customTimePeriodId">${uiLabelMap.BSSalesCustomTimePeriodId}</label>
	</div>
	<div class='span7'>
		<input type="text" id="wn_customTimePeriodId" class="span12" maxlength="20" value=""/>
	</div>
</div>
-->
<#--$("#wn_customTimePeriodId").jqxInput({height: 25, theme: theme, maxLength: 20});-->
<#--customTimePeriodId: $('#wn_customTimePeriodId').val(),-->
<#--{input: '#wn_customTimePeriodId', message: '${uiLabelMap.BSThisFieldMustNotByContainSpecialCharacter}', action: 'blur', rule: 
	function (input, commit) {
		return OlbValidatorUtil.validElement(input, commit, 'validCannotSpecialCharactor');
	}
},-->