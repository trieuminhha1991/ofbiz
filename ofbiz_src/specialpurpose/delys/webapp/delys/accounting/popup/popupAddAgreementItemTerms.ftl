<div id="alterpopupWindow" style="display:none;">
	<div>${uiLabelMap.accCreateNew}</div>
	<div style="overflow: hidden;">
		<div class='form-window-container'>
			<div class='form-window-content'>
				<form id="formAdd"/>
				<div class='row-fluid margin-bottom10'>
					<div class='span5 align-right'>
						<label class="asterisk">${uiLabelMap.termTypeId}</label>
					</div>  
					<div class="span7">
						<div id="termTypeIdAdd"></div>
			   		</div>		
				</div>
				<div class='row-fluid margin-bottom10'>
					<div class='span5 align-right'>
						<label class="asterisk">${uiLabelMap.invoiceItemTypeId}</label>
					</div>  
					<div class="span7">
						<div id="invoiceItemTypeIdAdd"></div>
			   		</div>		
				</div>
				<div class='row-fluid margin-bottom10'>
					<div class='span5 align-right'>
						<label class="asterisk">${uiLabelMap.fromDate}</label>
					</div>  
					<div class="span7">
						<div id="fromDateAdd"></div>
			   		</div>		
				</div>
				<div class='row-fluid margin-bottom10'>
					<div class='span5 align-right'>
						<label>${uiLabelMap.thruDate}</label>
					</div>  
					<div class="span7">
						<div id="thruDateAdd"></div>
			   		</div>		
				</div>
				<div class='row-fluid margin-bottom10'>
					<div class='span5 align-right'>
						<label>${uiLabelMap.termValue}</label>
					</div>  
					<div class="span7">
						<div id="termValueAdd">
	 					</div>
			   		</div>		
				</div>
				<div class='row-fluid margin-bottom10'>
					<div class='span5 align-right'>
						<label>${uiLabelMap.termDays}</label>
					</div>  
					<div class="span7">
						<div id="termDaysAdd">
	 					</div>
			   		</div>		
				</div>
				<div class='row-fluid margin-bottom10'>
					<div class='span5 align-right'>
						<label>${uiLabelMap.textValue}</label>
					</div>  
					<div class="span7">
						<div id="textValueAdd">
	 					</div>
			   		</div>		
				</div>
				<div class='row-fluid margin-bottom10'>
					<div class='span5 align-right'>
						<label>${uiLabelMap.FormFieldTitle_minQuantity}</label>
					</div>  
					<div class="span7">
						<div id="minQuantityAdd">
	 					</div>
			   		</div>		
				</div>
				<div class='row-fluid margin-bottom10'>
					<div class='span5 align-right'>
						<label>${uiLabelMap.FormFieldTitle_maxQuantity}</label>
					</div>  
					<div class="span7">
						<div id="maxQuantityAdd">
	 					</div>
			   		</div>		
				</div>
				<div class='row-fluid margin-bottom10'>
					<div class='span5 align-right'>
						<label>${uiLabelMap.description}</label>
					</div>  
					<div class="span7">
						<input id="descriptionAdd">
	 					</input>
			   		</div>		
				</div>
				</form>
			</div>
		</div>
		<div class="form-action">
			<button id="cancel" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.Cancel}</button>
			<button id="saveAndContinue" class='btn btn-success form-action-button pull-right'><i class='fa-plus'></i> ${uiLabelMap.SaveAndContinue}</button>
			<button id="save" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.Save}</button>
		</div>
	</div>
</div>				

<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxnumberinput.js"></script>
<script>
	$.jqx.theme = 'olbius';  
	theme = $.jqx.theme;
	var popupAction = (function(){
		var initElement = function(){
			$("#fromDateAdd").jqxDateTimeInput({width: '230px', height: '25px', formatString: 'dd-MM-yyyy hh:mm:ss',value : null,allowNullDate : true});
			var date = new Date();
			$("#fromDateAdd").jqxDateTimeInput('setMinDate',new Date(date.getYear() + 1900,date.getMonth(),date.getDate()));
			$("#thruDateAdd").jqxDateTimeInput({width: '230px', height: '25px', formatString: 'dd-MM-yyyy hh:mm:ss',value : null,allowNullDate : true});
			$("#thruDateAdd").jqxDateTimeInput('val', null);
			$("#termTypeIdAdd").jqxDropDownList({source: ttData, displayMember: "description", valueMember: "termTypeId", height: 25, width: 230 ,placeHolder : '<span style=\"color: inherit; border: none; padding-top: 0px; padding-bottom: 0px; background-color: transparent;\">${uiLabelMap.PleaseChooseAcc}</span>'});
			$("#invoiceItemTypeIdAdd").jqxDropDownList({source: iitData, displayMember: "description", valueMember: "invoiceItemTypeId", height: 25, width: 230,placeHolder : '<span style=\"color: inherit; border: none; padding-top: 0px; padding-bottom: 0px; background-color: transparent;\">${uiLabelMap.PleaseChooseAcc}</span>'});
			$("#termValueAdd").jqxNumberInput({width: 230,digits : 15,decimalDigits : 2,max:9999999999999,min : 0});
			$("#termDaysAdd").jqxNumberInput({width: 230,decimalDigits : 0,min : 0});
			$("#textValueAdd").jqxNumberInput({width: 230,decimalDigits : 0,min : 0});
			$("#minQuantityAdd").jqxNumberInput({width: 230,decimalDigits : 0,min : 0});
			$("#maxQuantityAdd").jqxNumberInput({width: 230,decimalDigits : 0,min : 0});
			$("#descriptionAdd").jqxInput({width: 225});
			$("#alterpopupWindow").jqxWindow({
		        width: 600,height : 500, resizable: true,  isModal: true, autoOpen: false, cancelButton: $("#cancel"), modalOpacity: 0.7          
		    });
		}
		var bindEvent = function(){
		 // update the edited row when the user clicks the 'Save' button.
		    $("#save").click(function () {
		    	if(save()){
		    		$("#alterpopupWindow").jqxWindow('close');
		    		 clear();
		    		 $('#formAdd').jqxValidator('hide');
		    	};
		    });
		    
		    $('#saveAndContinue').click(function(){
		    	saveAndContinue();
		    })
		    
		    $('#cancel').click(function(){
		    	clear();
		    	 $('#formAdd').jqxValidator('hide');
		    })
		}
		
		var initRules = function(){
			$('#formAdd').jqxValidator({
				rules : [
					{input : '#termTypeIdAdd',message : '${StringUtil.wrapString(uiLabelMap.FieldRequiredAccounting?default(''))}',action : 'blur,change',rule : function(){
							var val = $('#termTypeIdAdd').jqxDropDownList('val');
							if(!val) return false;
							return true;	
					}},{input : '#invoiceItemTypeIdAdd',message : '${StringUtil.wrapString(uiLabelMap.FieldRequiredAccounting?default(''))}',action : 'blur,change',rule : function(){
							var val = $('#invoiceItemTypeIdAdd').jqxDropDownList('val');
							if(!val) return false;
							return true;	
					}},
					{input : '#fromDateAdd',message : '${StringUtil.wrapString(uiLabelMap.FieldRequiredAccounting?default(''))}',action : 'click,change,close',rule : function(){
						var val = $('#fromDateAdd').jqxDateTimeInput('val');
						if(!val) return false;
						return true;
					}},
					{input : '#fromDateAdd',message : '${StringUtil.wrapString(uiLabelMap.fromDateValidate?default(''))}',action : 'click,change,close',rule : function(){
						var val = ($('#fromDateAdd').jqxDateTimeInput('getDate') != null) ? $('#fromDateAdd').jqxDateTimeInput('getDate').setHours(0,0,0,0) : '';
						var valThru = ($('#thruDateAdd').jqxDateTimeInput('getDate') !=null) ? $('#thruDateAdd').jqxDateTimeInput('getDate').setHours(0,0,0,0) : '';
						var now  = (new Date()).setHours(0,0,0,0);
						if((val && !valThru) || (val && valThru)){
							return true;
						}else if(val < now || val >= valThru) return false;
						return true;
					}},
					{input : '#thruDateAdd',message : '${StringUtil.wrapString(uiLabelMap.thruDateValidate?default(''))}',action : 'click,change,close',rule : function(){
						var val = $('#fromDateAdd').jqxDateTimeInput('getDate');
						var valThru = $('#thruDateAdd').jqxDateTimeInput('getDate');
						var now  = new Date();
						if(!valThru){
							return true;
						}else if(valThru < now || val >= valThru) return false;
						return true;
					}},
					{input : '#minQuantityAdd',message : '${StringUtil.wrapString(uiLabelMap.MinQuantityMaxQuantity?default(''))}',action : 'click,change,close',rule : function(){
						var	min = $('#minQuantityAdd').jqxNumberInput('val');
						var max = $('#maxQuantityAdd').jqxNumberInput('val');
						min = parseInt(min);
						max = parseInt(max);
						if((!min && !max) || (!max && min) || (!min && max)){
							 return true; 
						}else if(min >= max){
							return false;
						}
						return true;
					}},
					{input : '#maxQuantityAdd',message : '${StringUtil.wrapString(uiLabelMap.MinQuantityMaxQuantity?default(''))}',action : 'click,change,close',rule : function(){
						var	min = $('#minQuantityAdd').jqxNumberInput('val');
						var max = $('#maxQuantityAdd').jqxNumberInput('val');
						min = parseInt(min);
						max = parseInt(max);
						if((!max && !min) || (!max && min) || (max && !min)){
							return true;
						}else if(min >= max){
							return false;
						} 
						return true;
					}}
					
				]
			})
		}
		
		var clear = function(){
				$('#termTypeIdAdd').jqxDropDownList('clearSelection');
				$('#invoiceItemTypeIdAdd').jqxDropDownList('clearSelection');
				$('#termTypeIdAdd').jqxDateTimeInput('val',null);
				$("#fromDateAdd").jqxDateTimeInput('val', null);
				$("#thruDateAdd").jqxDateTimeInput('val', null);
				$("#termValueAdd").jqxNumberInput('clear');	
				$("#termDaysAdd").jqxNumberInput('clear');		
				$("#textValueAdd").jqxNumberInput('clear');
				$("#minQuantityAdd").jqxNumberInput('clear');
				$("#maxQuantityAdd").jqxNumberInput('clear');
				$("#descriptionAdd").jqxInput('val', null);    	
		}
		
		var save  =  function(){
			if(!$('#formAdd').jqxValidator('validate')){return false;}
	    	var row;
	    	var checkNull = $('#thruDateAdd').val();
	    	if (checkNull != '' && typeof(checkNull) != 'undefined')
	    	{
	    		checkNull = $('#thruDateAdd').jqxDateTimeInput('getDate').getTime();
	    	}
	        row = { 
	        		termTypeId:$('#termTypeIdAdd').val(),         		
	        		fromDate:$('#fromDateAdd').jqxDateTimeInput('getDate').getTime(),  			        		
	        		thruDate:checkNull,   	
	        		invoiceItemTypeId:$('#invoiceItemTypeIdAdd').val(),
	        		termValue:$('#termValueAdd').val(),
	        		termDays:$('#termDaysAdd').val(),
	        		textValue:$('#textValueAdd').val(),
	        		minQuantity:$('#minQuantityAdd').val(),
	        		maxQuantity:$('#maxQuantityAdd').val(),
	        		description:$('#descriptionAdd').val(),
	        	  };
		   $("#jqxgrid").jqxGrid('addRow', null, row, "first");
	        // select the first row and clear the selection.
	        $("#jqxgrid").jqxGrid('clearSelection');                        
	        $("#jqxgrid").jqxGrid('selectRow', 0);  
	        return true;
	    }
	    
	    var saveAndContinue = function(){
	    	if(!save()){
	    		return false;
	    	}
	    	$('#formAdd').jqxValidator('hide');
	    }
	    
	    return {
	    	init : function(){
	    		initElement();
	    		bindEvent();
	    		initRules();
	    	}
	    }
	}())
	
	$(document).ready(function(){
		popupAction.init();
	})
</script>