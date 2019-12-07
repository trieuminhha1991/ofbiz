<div id="popupAddRow" class='hide'>
    <div>${uiLabelMap.CreateNewParameters}</div>
    <div class="form-window-container">
    	<div class="form-window-content">
    		<form>
				<div class='row-fluid margin-bottom10'>
					<div class='span5 text-algin-right'>
						<label class="asterisk">${uiLabelMap.parameterCode}</label>
					</div>
					<div class="span7">	
						<input type="text" name="codeadd" id="codeadd" />
					</div>
				</div>
				<div class='row-fluid margin-bottom10'>
					<div class='span5 text-algin-right'>
						<label class=" asterisk" >${uiLabelMap.parameterName}</label>
					</div>	
					<div class="span7">
						<input type="text" name="nameadd" id="nameadd"/>
					</div>
				</div>
				<div class='row-fluid margin-bottom10'>
					<div class='span5 text-algin-right'>
						<label class="" >${uiLabelMap.CommonCharacteristic}</label>
					</div>	
					<div class="span7">
						<div id="characteristicDropDown"></div>
					</div>
				</div>
				<div class='row-fluid margin-bottom10'>
					<div class='span5 text-algin-right'>
						<label class=' asterisk'>${uiLabelMap.CalcPeriod}</label>
					</div>	
					<div class="span7">
						<div id="periodTypeIdDd">
		   				</div>
					</div>
				</div>
				<div class='row-fluid margin-bottom10'>
					<div class='span5 text-algin-right'>
						<label class=" asterisk" >${uiLabelMap.parameterType}</label>
					</div>
					<div class="span7">
						<div id="parameterTypeAdd"></div>
					</div>
				</div>
				<div class='row-fluid margin-bottom10'>
					<div class='span5 text-algin-right'>
						<label class="" >${uiLabelMap.HrolbiusDefaultValue}</label>
					</div>
					<div class="span7">
						<input type="text" name="actualvalueadd" id="actualvalueadd" />
					</div>
				</div>
				
	    	</form>
    	</div>
		<div class="form-action">
			<button id="cancelBtn" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.Cancel}</button>
			<button type="button" class='btn btn-primary form-action-button pull-right' id="saveBtn">
				<i class='fa fa-check'></i>&nbsp;${uiLabelMap.CommonSave}</button>
		</div>   	
    </div>
</div>  
<script src="/hrresources/js/CommonFunction.js" type="text/javascript"></script>
<script type="text/javascript" src="/hrresources/js/payroll/CreateParameters.js"></script>
<script type="text/javascript">
	if(typeof(uiLabelMap) == "undefined"){
		uiLabelMap = {};	
	}
	uiLabelMap.FieldRequired = "${StringUtil.wrapString(uiLabelMap.FieldRequired?default(''))}";
	uiLabelMap.CommonAnd = "${StringUtil.wrapString(uiLabelMap.CommonAnd?default(''))}";
	uiLabelMap.IdNotSpace = "${StringUtil.wrapString(uiLabelMap.IdNotSpace?default(''))}";
	uiLabelMap.FieldRequired = "${StringUtil.wrapString(uiLabelMap.FieldRequired?default(''))}";
	uiLabelMap.FieldRequired = "${StringUtil.wrapString(uiLabelMap.FieldRequired?default(''))}";
	uiLabelMap.FieldRequired = "${StringUtil.wrapString(uiLabelMap.FieldRequired?default(''))}";
	uiLabelMap.FieldRequired = "${StringUtil.wrapString(uiLabelMap.FieldRequired?default(''))}";
</script>