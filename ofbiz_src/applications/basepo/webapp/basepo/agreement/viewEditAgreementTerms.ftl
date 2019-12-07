<div id="alterpopupWindow" style="display:none;">
	<div>${uiLabelMap.accCreateNew}</div>
	<div style="overflow: hidden;">
		<div id="containerSAC"></div>
		<div class="form-window-content">
			<div class="row-fluid margin-bottom10">
				<div class="span5 text-algin-right">
					<label class="asterisk">${uiLabelMap.termTypeId}</label>
				</div>  
				<div class="span7">
					<div id="termTypeIdAdd"></div>
				</div>
			</div>
			<div class="row-fluid margin-bottom10">
				<div class="span5 text-algin-right">
					<label class="asterisk">${uiLabelMap.invoiceItemTypeIdPO}</label>
				</div>  
				<div class="span7">
					<div id="invoiceItemTypeIdAdd"></div>
				</div>
			</div>
			<div class="row-fluid margin-bottom10">
				<div class="span5 text-algin-right">
					<label class="asterisk">${uiLabelMap.fromDate}</label>
				</div>  
				<div class="span7">
					<div id="fromDateAdd"></div>
				</div>
			</div>
			<div class="row-fluid margin-bottom10">
				<div class="span5 text-algin-right">
					<label>${uiLabelMap.thruDate}</label>
				</div>  
				<div class="span7">
					<div id="thruDateAdd"></div>
				</div>
			</div>
			<div class="row-fluid margin-bottom10">
				<div class="span5 text-algin-right">
					<label>${uiLabelMap.termValue}</label>
				</div>  
				<div class="span7">
					<div id="termValueAdd"></div>
				</div>
			</div>
			<div class="row-fluid margin-bottom10">
				<div class="span5 text-algin-right">
					<label>${uiLabelMap.termDays}</label>
				</div>  
				<div class="span7">
					<div id="termDaysAdd"></div>
				</div>
			</div>
			<div class="row-fluid margin-bottom10">
				<div class="span5 text-algin-right">
					<label>${uiLabelMap.textValue}</label>
				</div>  
				<div class="span7">
					<input id="textValueAdd"></input>
				</div>
			</div>
			<div class="row-fluid margin-bottom10">
				<div class="span5 text-algin-right">
					<label>${uiLabelMap.FormFieldTitle_minQuantity}</label>
				</div>  
				<div class="span7">
					<div id="minQuantityAdd"></div>
				</div>
			</div>
			<div class="row-fluid margin-bottom10">
				<div class="span5 text-algin-right">
					<label>${uiLabelMap.FormFieldTitle_maxQuantity}</label>
				</div>  
				<div class="span7">
					<div id="maxQuantityAdd"></div>
				</div>
			</div>
			<div class="row-fluid margin-bottom10">
				<div class="span5 text-algin-right">
					<label>${uiLabelMap.description}</label>
				</div>  
				<div class="span7">
					<textarea id="descriptionAdd" style="resize: none; margin-top: 0; width: 218px;" rows="3"></textarea>
				</div>
			</div>
		</div>
		<div class="form-action">
			<button id="cancel" class="btn btn-danger form-action-button pull-right"><i class="fa-remove"></i> ${uiLabelMap.Cancel}</button>
			<button id="saveAndContinue" class="btn btn-success form-action-button pull-right"><i class="fa-plus"></i> ${uiLabelMap.SaveAndContinue}</button>
			<button id="save" class="btn btn-primary form-action-button pull-right"><i class="fa-check"></i> ${uiLabelMap.Save}</button>
		</div>
	</div>
</div>
<script>

	//Create theme
	var action = (function(){
		var initElement = function(){
			$("#termTypeIdAdd").jqxDropDownList({source: ttData, displayMember: "description", valueMember: "termTypeId", height: 25, width: 230, filterable : true,placeHolder :"<span style=\"color: inherit; border: none; padding-top: 0px; padding-bottom: 0px; background-color: transparent;\">${uiLabelMap.PleaseChooseAcc}</span>" });
			$("#invoiceItemTypeIdAdd").jqxDropDownList({source: iitData, displayMember: "description", valueMember: "invoiceItemTypeId", height: 25, width: 230, filterable : true,placeHolder :"<span style=\"color: inherit; border: none; padding-top: 0px; padding-bottom: 0px; background-color: transparent;\">${uiLabelMap.PleaseChooseAcc}</span>"  });
			var date = new Date();
			$("#fromDateAdd").jqxDateTimeInput({width: "230px", height: "25px", formatString: "dd-MM-yyyy", clearString: "${uiLabelMap.Clear}", todayString: "${uiLabelMap.Today}",showFooter:true});
			$("#fromDateAdd").jqxDateTimeInput("setMinDate",new Date(date.getYear() + 1900,date.getMonth(),date.getDate()));
			$("#thruDateAdd").jqxDateTimeInput({width: "230px", height: "25px", formatString: "dd-MM-yyyy", clearString: "${uiLabelMap.Clear}", todayString: "${uiLabelMap.Today}",showFooter:true});
			$("#thruDateAdd").jqxDateTimeInput("val", null);
			$("#termValueAdd").jqxNumberInput({ width:  230, height: 24,  max : 999999999999999999, digits: 18, decimalDigits:2, spinButtons: false, min: 0});
			$("#termDaysAdd").jqxNumberInput({ width:  230, height: 24, decimalDigits: 0, spinButtons: false, min: 0});
			$("#textValueAdd").jqxInput({ width:  225, height: 24});
			$("#minQuantityAdd").jqxNumberInput({ width:  230, height: 24, decimalDigits: 0, spinButtons: false, min: 0});
			$("#maxQuantityAdd").jqxNumberInput({ width:  230, height: 24, decimalDigits: 0, spinButtons: false, min: 0});
		};
		var initWindow = function(){
			$("#alterpopupWindow").jqxWindow({
				width: 550, height: 550, resizable: true,  isModal: true, autoOpen: false, cancelButton: $("#cancel"), modalOpacity: 0.7          
			});	
			$("#alterpopupWindow").on("close", function (event) {
				action.clear();
			});
		};
		var initRule = function(popup){
			popup.jqxValidator({
				rules: [{
					input: "#termTypeIdAdd", 
					message: "${StringUtil.wrapString(uiLabelMap.FieldRequiredAccounting?default(''))}",
					action: "change, blur", 
					rule: function (input, commit) {
						var val = input.jqxDropDownList("val");
						if(!val) return false;
						return true;
					}
				},{
					input: "#invoiceItemTypeIdAdd", 
					message: "${StringUtil.wrapString(uiLabelMap.FieldRequiredAccounting?default(''))}",
					action: "change, blur", 
					rule: function (input, commit) {
						var val = input.jqxDropDownList("val");
						if(!val) return false;
						return true;
					}
				},{
					input: "#fromDateAdd", 
					message: "${StringUtil.wrapString(uiLabelMap.FieldRequiredAccounting?default(''))}",
					action: "blur", 
					rule: function (input, commit) {
						var from = input.jqxDateTimeInput("val");
						if(!from){
							return false;
						}
						return true;
					}
				}, {
					input: "#fromDateAdd", 
					message: "${StringUtil.wrapString(uiLabelMap.FromDateThruDate?default(''))}", 
					action: "blur,change", 
					rule: function (input, commit) {
						var from = input.jqxDateTimeInput("getDate");
						var thru = $("#thruDateAdd").jqxDateTimeInput("getDate");
						if (from && thru){
							return from <= thru;	
						} else if (from && !thru) {
							return true;
						}
						return false;
					}
				}, {
					input: "#thruDateAdd", 
					message: "${StringUtil.wrapString(uiLabelMap.FromDateThruDate?default(''))}", 
					action: "blur,change", 
					rule: function (input, commit) {
						var from = $("#fromDateAdd").jqxDateTimeInput("getDate");
						var thru = input.jqxDateTimeInput("getDate");
						if (from && thru) {
							return from <= thru;	
						} else if (from && !thru) {
							return true;
						}
						return false;
					}
				},{
					input: "#minQuantityAdd", 
					message: "${StringUtil.wrapString(uiLabelMap.MinQuantityMaxQuantity?default(''))}", 
					action: "change,blur,keyup", 
					rule: function (input, commit) {
						var min = $("#minQuantityAdd").jqxNumberInput("val");
							var max = $("#maxQuantityAdd").jqxNumberInput("val");
							min = parseInt(min);
							max = parseInt(max);
							if ((!min && !max) || (!min && max)) {
								 return true; 
							} else if (min >= max) {
								return false;
							}
							return true;
					}
				},{
					input: "#maxQuantityAdd", 
					message: "${StringUtil.wrapString(uiLabelMap.MinQuantityMaxQuantity?default(''))}", 
					action: "change,blur,keyup",
					rule: function (input, commit) {
						var	min = $("#minQuantityAdd").jqxNumberInput("val");
						var max = $("#maxQuantityAdd").jqxNumberInput("val");
						if(max) $("#minQuantityAdd").trigger("change");
						min = parseInt(min);
						max = parseInt(max);
						if ((!max && !min) || (max && !min)) {
							return true;
						} else if(min >= max) {
							return false;
						} 
						return true;
					}
				}],
				scroll: false
			});
		};
		var bindEvent = function(){	
			$("#save").click(function () {
				if(!action.save()){
					return;
				}
				$("#alterpopupWindow").jqxWindow("close");
			});
			$("#saveAndContinue").click(function () {
				action.save();
			});
		};
		var save = function(){
			if(!$("#alterpopupWindow").jqxValidator("validate")){
				return false;
			}
			var checkNull = $("#thruDateAdd").val();
			if (checkNull != "")
			{
				checkNull = $("#thruDateAdd").jqxDateTimeInput("getDate").getTime();
			}
			var row = { 
					termTypeId:$("#termTypeIdAdd").val(),         		
					fromDate:$("#fromDateAdd").jqxDateTimeInput("getDate").getTime(),  			        		
					thruDate:checkNull,   	
					invoiceItemTypeId:$("#invoiceItemTypeIdAdd").val(),
					termValue:$("#termValueAdd").val(),
					termDays:$("#termDaysAdd").val(),
					textValue:$("#textValueAdd").val(),
					minQuantity:$("#minQuantityAdd").val(),
					maxQuantity:$("#maxQuantityAdd").val(),
					description:$("#descriptionAdd").val(),
			};
			$("#jqxgrid").jqxGrid("addRow", null, row, "first");
			// select the first row and clear the selection.
			$("#jqxgrid").jqxGrid("clearSelection");                        
			return true;
		};
		var clearElement = function(){
			$("#termTypeIdAdd").jqxDropDownList("clearSelection");
			$("#invoiceItemTypeIdAdd").jqxDropDownList("clearSelection");
			$("#fromDateAdd").jqxDateTimeInput("val", null);
			$("#thruDateAdd").jqxDateTimeInput("val", null);
			$("#termValueAdd").jqxNumberInput("clear");
			$("#termDaysAdd").jqxNumberInput("clear");
			$("#textValueAdd").jqxInput("val","");
			$("#minQuantityAdd").jqxNumberInput("clear");
			$("#maxQuantityAdd").jqxNumberInput("clear");
			$("#alterpopupWindow").jqxValidator("hide");
			$("#descriptionAdd").val("");
		};
		return {
			init: function(){
				initElement();
				initWindow();
				initRule($("#alterpopupWindow"));
				bindEvent();
			},
			save: save,
			clear: clearElement
		};
	}());
	$(document).ready(function(){
		action.init();
	});
</script>