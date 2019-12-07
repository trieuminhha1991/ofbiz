<div id="alterpopupWindow" class='hide'>
	<div>${uiLabelMap.accCreateNew}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<div class='row-fluid margin-bottom10'>
				<div class='span5 text-algin-right'>
					<label class="asterisk">${uiLabelMap.FormFieldTitle_paymentGroupTypeId}</label>
				</div>  
				<div class="span7">
					<div id="paymentGroupTypeIdAdd"></div>
		   		</div>		
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class='span5 text-algin-right'>
					<label class="asterisk">${uiLabelMap.FormFieldTitle_paymentGroupName}</label>
				</div>  
				<div class="span7">
					<input id="paymentGroupNameAdd"/>
		   		</div>		
			</div>
		</div>
		<div class="form-action">
			<button id="cancel" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.Cancel}</button>
			<button id="saveAndContinue" class='btn btn-success form-action-button pull-right'><i class='fa-plus'></i> ${uiLabelMap.SaveAndContinue}</button>
			<button id="save" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.Save}</button>
		</div>
	</div>
</div>
<script type="text/javascript">
	var paymentGroup = (function(){
		$.jqx.theme = 'olbius';
		var theme = $.jqx.theme;
		var grid = $("#jqxgrid");
		var popup = $("#alterpopupWindow");
		var initWindow = function(){
			popup.jqxWindow({
				width: 480, height: 180, resizable: false,  isModal: true, autoOpen: false, cancelButton: $("#cancel"), modalOpacity: 0.7, theme:theme           
			});
		};
		var initElement = function(){
			$('#paymentGroupTypeIdAdd').jqxDropDownList({placeHolder: '${StringUtil.wrapString(uiLabelMap.ChoosePaymentGroup)}', source: payGrTypeData, displayMember: "description", valueMember: "paymentGroupTypeId", autoDropDownHeight: true});
			$('#paymentGroupNameAdd').jqxInput({width: '195px'});
		};
		var bindEvent = function(){
			$("#save").click(function () {
				if(!saveAction()){
					return;
				}
		   		$("#alterpopupWindow").jqxWindow('close');
			});
			$("#saveAndContinue").click(function () {
				saveAction();
			});
		};
		var saveAction = function(){
			if(!popup.jqxValidator('validate')){
				return false;
			}
			var row = { 
				paymentGroupTypeId: $('#paymentGroupTypeIdAdd').val(),
				paymentGroupName: $('#paymentGroupNameAdd').val()
    	  	};
			grid.jqxGrid('addRow', null, row, "first");
		    grid.jqxGrid('clearSelection');                        
		    grid.jqxGrid('selectRow', 0);  
		    return true;
		};
		var initRule = function(){
	       	popup.jqxValidator({
	       	   	rules: [{
	                   input: "#paymentGroupTypeIdAdd", 
	                   message: "${StringUtil.wrapString(uiLabelMap.FieldRequiredAccounting?default(''))}", 
	                   action: 'blur', 
	                   rule: function (input, commit) {
	                       var val = input.jqxDropDownList('getSelectedIndex');
	                       return val != -1;
	                   }
	               },{
	                   input: "#paymentGroupNameAdd", 
	                   message: "${StringUtil.wrapString(uiLabelMap.FieldRequiredAccounting?default(''))}", 
	                   action: 'blur', 
	                   rule: 'required'
	               }]
	       	 });
		};
		return {
			init: function(){
				initWindow();
				initElement();
				bindEvent();
				initRule();
			}
		};
	})();
	$(document).ready(function(){
		paymentGroup.init();
	});
</script>