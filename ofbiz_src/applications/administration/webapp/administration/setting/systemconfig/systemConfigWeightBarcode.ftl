<div id="container" class="container-noti"></div>
<div id="jqxNotification" style="margin-bottom:5px">
    <div id="notificationContent">
    </div>
</div>
<div id="systemConfigWeightBarcode" class="row-fluid form-horizontal form-window-content-custom">
	<div class="span6">
		<div class='row-fluid'>
			<div class='span5'>
				<label class="required">${uiLabelMap.BSWeightBarcodeType}</label>
			</div>
			<div class="span7">
				<div id="weightBarcodeType"></div>
	   		</div>
		</div>
		<div class='row-fluid'>
			<div class='span5'>
				<label class="required">${uiLabelMap.BSPrefix}</label>
			</div>
			<div class="span7">
				<div id="prefixWeightBarcode"></div>
	   		</div>
		</div>
		<div class='row-fluid'>
			<div class='span5'>
				<label class="required">${uiLabelMap.BSPattern}</label>
			</div>
			<div class="span7">
				<div id="patternWeightBarcode"></div>
	   		</div>
		</div>
		<div class='row-fluid'>
			<div class='span5'>
				<label class="required">${uiLabelMap.BSDecimalsInWeight}</label>
			</div>
			<div class="span7">
				<div id="decimalsInWeight"></div>
	   		</div>
		</div>
	</div><!--.span6-->
	<div class="span6">
	</div><!--.span6-->
</div>
<div class="pull-right">
	<button id="alterSave" class='btn btn-primary form-action-button'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
	<button id="alterCancel" class='btn btn-danger form-action-button'><i class='fa-remove'></i> ${uiLabelMap.BSResetEdit}</button>
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

<@jqGridMinimumLib />
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>
<@jqOlbCoreLib hasGrid=true hasDropDownButton=true hasDropDownList=true hasComboBox=true hasValidator=true/>
<script type="text/javascript">
	<#assign WeightBarcodeType = delegator.findOne("SystemConfig", {"systemConfigId": "WeightBarcodeType"}, false)!/>
	<#assign PrefixWeightBarcode = delegator.findOne("SystemConfig", {"systemConfigId": "PrefixWeightBarcode"}, false)!/>
	<#assign PatternWeightBarcode = delegator.findOne("SystemConfig", {"systemConfigId": "PatternWeightBarcode"}, false)!/>
	<#assign DecimalsInWeight = delegator.findOne("SystemConfig", {"systemConfigId": "DecimalsInWeight"}, false)!/>
	$(function(){
		SystemConfigWeightBC.init();
	});
	var SystemConfigWeightBC = (function(){
		var weightBarcodeTypeDDL;
		var prefixWeightBarcodeDDL;
		var patternWeightBarcodeDDL;
		var validatorVAL;
		
		var init = function(){
			initElement();
			initElementComplex();
			initEvent();
			initValidateForm();
		};
		var initElement = function(){
			jOlbUtil.notification.create($("#container"), $("#jqxNotification"));
			jOlbUtil.numberInput.create($("#decimalsInWeight"), {width: '100%', spinButtons: true, decimalDigits: 0, min: 0, max: 5, decimal: 1, inputMode: 'simple'});
			
			<#if DecimalsInWeight?exists>$("#decimalsInWeight").jqxNumberInput("val", "${DecimalsInWeight.systemValue?if_exists}");</#if>
		};
		var initElementComplex = function(){
			var weightBarcodeTypeData = [
				{id: "UPCA", description: "UPCA"},
				{id: "EAN13", description: "EAN13"},
			];
			var configWeightBarcodeType = {
				width:'100%',
				key: "id",
	    		value: "description",
	    		displayDetail: false,
				dropDownWidth: 'auto',
				placeHolder: "${StringUtil.wrapString(uiLabelMap.BSClickToChoose)}",
				autoDropDownHeight: true,
				disabled: true
			};
			weightBarcodeTypeDDL = new OlbDropDownList($("#weightBarcodeType"), weightBarcodeTypeData, configWeightBarcodeType, <#if WeightBarcodeType?exists>["${WeightBarcodeType.systemValue?if_exists}"]<#else>null</#if>);
			
			var prefixWeightBarcodeEANData = [
				{id: "02", description: "02"},
				{id: "20", description: "20"},
				{id: "21", description: "21"},
				{id: "22", description: "22"},
				{id: "23", description: "23"},
				{id: "24", description: "24"},
				{id: "25", description: "25"},
				{id: "26", description: "26"},
				{id: "27", description: "27"},
				{id: "28", description: "28"},
				{id: "29", description: "29"},
			];
			var prefixWeightBarcodeType = {
				width:'100%',
				key: "id",
	    		value: "description",
	    		displayDetail: false,
				dropDownWidth: 'auto',
				placeHolder: "${StringUtil.wrapString(uiLabelMap.BSClickToChoose)}",
				autoDropDownHeight: true,
			};
			prefixWeightBarcodeDDL = new OlbDropDownList($("#prefixWeightBarcode"), prefixWeightBarcodeEANData, prefixWeightBarcodeType, <#if PrefixWeightBarcode?exists>["${PrefixWeightBarcode.systemValue?if_exists}"]<#else>null</#if>);
			
			var patternWeightBarcodeEANData = [
				{id: "IIIIIWWWWW", description: "5I-5W: Prefix/5 Code/5 Weight/Check digit"},
			];
			var patternWeightBarcodeType = {
				width:'100%',
				key: "id",
	    		value: "description",
	    		displayDetail: false,
				dropDownWidth: 'auto',
				placeHolder: "${StringUtil.wrapString(uiLabelMap.BSClickToChoose)}",
				autoDropDownHeight: true,
			};
			patternWeightBarcodeDDL = new OlbDropDownList($("#patternWeightBarcode"), patternWeightBarcodeEANData, patternWeightBarcodeType, <#if PatternWeightBarcode?exists>["${PatternWeightBarcode.systemValue?if_exists}"]<#else>null</#if>);
			
		};
		var initEvent = function(){
			$("#alterSave").on("click", function(){
				if (!validatorVAL.validate()) {
					return false;
				};
				jOlbUtil.confirm.dialog("${StringUtil.wrapString(uiLabelMap.BSAreYouSureYouWantToUpdate)}?", 
					function(){
						updateSystemConfig();
					}
				);
			});
			$("#alterCancel").on("click", function(){
				jOlbUtil.confirm.dialog("${StringUtil.wrapString(uiLabelMap.BSThisActionWillClearAllTypingDataAreYouSure)}", 
					function(){
						location.reload();
					}
				);
			});
		};
		var updateSystemConfig = function(){
			$("#alterSave").addClass("disabled");
			$("#alterCancel").addClass("disabled");
			
			var dataMap = {
				"weightBarcodeType": weightBarcodeTypeDDL.getValue(),
				"prefixWeightBarcode": prefixWeightBarcodeDDL.getValue(),
				"patternWeightBarcode": patternWeightBarcodeDDL.getValue(),
				"decimalsInWeight": $("#decimalsInWeight").val()
			};
			
			$.ajax({
				type: 'POST',
				url: 'updateSystemConfigWB',
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
					        	
					        	$("#alterSave").removeClass("disabled");
								$("#alterCancel").removeClass("disabled");
								
					        	return false;
							}, function(){
								$('#container').empty();
					        	$('#jqxNotification').jqxNotification({ template: 'info'});
					        	$("#jqxNotification").html("${StringUtil.wrapString(uiLabelMap.wgupdatesuccess)}");
					        	$("#jqxNotification").jqxNotification("open");
				        		//location.reload();
							}
					);
				},
				error: function(data){
					alert("Send request is error");
					$("#alterSave").removeClass("disabled");
					$("#alterCancel").removeClass("disabled");
				},
				complete: function(data){
					$("#loader_page_common").hide();
				},
			});
		};
		var initValidateForm = function(){
			var extendRules = [];
			var mapRules = [
					{input: '#weightBarcodeType', type: 'validObjectNotNull', objType: 'dropDownList'},
					{input: '#prefixWeightBarcode', type: 'validObjectNotNull', objType: 'dropDownList'},
					{input: '#patternWeightBarcode', type: 'validObjectNotNull', objType: 'dropDownList'},
					{input: '#decimalsInWeight', type: 'validInputNotNull'},
	            ];
			validatorVAL = new OlbValidator($('#systemConfigWeightBarcode'), mapRules, extendRules, {position: 'bottom'});
		};
		return {
			init: init
		}
	}());
</script>