 <div id="alterpopupWindow" style="display:none;">
    <div>${uiLabelMap.accCreateNew}</div>
    <div style="overflow: hidden;">
		<div class='row-fluid form-window-content'>
			<form name="formAdd" id="formAdd">
    			<div class='row-fluid margin-bottom10'>
    				<div class='span5 align-right asterisk'>
    					${uiLabelMap.FormFieldTitle_fromDate}
    				</div>
    				<div class='span7'>
    					<div id="fromDateAdd"></div>
    				</div>
				</div>
				<div class='row-fluid margin-bottom10'>
    				<div class='span5 align-right '>
    					${uiLabelMap.FormFieldTitle_thruDate}
    				</div>
    				<div class='span7'>
    					<div id="thruDateAdd"></div>
    				</div>
				</div>
				<div class='row-fluid margin-bottom10'>
    				<div class='span5 align-right '>
    					${uiLabelMap.FormFieldTitle_registrationDate}
    				</div>
    				<div class='span7'>
    					<div id="registrationDateAdd"></div>
    				</div>
				</div>
				<div class='row-fluid margin-bottom10'>
    				<div class='span5 align-right '>
    					${uiLabelMap.AccountingFixedAssetGovAgencyPartyId}
    				</div>
    				<div class='span7' id="lookupPt">
    					<@htmlTemplate.lookupField name="govAgencyPartyIdAdd" id="govAgencyPartyIdAdd" value='' size="14" width="900" height="600" zIndex="18005"
								formName="formAdd"  fieldFormName="LookupJQPartyName" title="${uiLabelMap.CommonSearch} ${uiLabelMap.AccountingFixedAssetGovAgencyPartyId}" />
    				</div>
				</div>
				<div class='row-fluid margin-bottom10'>
    				<div class='span5 align-right '>
    					${uiLabelMap.AccountingFixedAssetRegNumber}
    				</div>
    				<div class='span7'>
    					<div id="registrationNumberAdd"></div>
    				</div>
				</div>
				<div class='row-fluid margin-bottom10'>
    				<div class='span5 align-right '>
    					${uiLabelMap.AccountingFixedAssetLicenseNumber}
    				</div>
    				<div class='span7'>
    					<div id="licenseNumberAdd"></div>
    				</div>
				</div>
			</form>
		</div>
		<div class="form-action">
			<div class='row-fluid'>
				<div class="span12 margin-top10">
					<button id="cancelCategory" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.Cancel}</button>
					<button id="saveAndContinueCategory" class='btn btn-success form-action-button pull-right'><i class='fa-plus'></i> ${uiLabelMap.SaveAndContinue}</button>
					<button id="saveCategory" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.Save}</button>
				</div>
			</div>
		</div>	
	</div>
</div>	
 
<script type="text/javascript">
	$.jqx.theme = 'olbius';  
	theme = $.jqx.theme;
    
	var action = (function(){
		 var initElement = function(){
		 	$('#fromDateAdd').jqxDateTimeInput({width: '200px', allowNullDate : true,value : null,height: '25px', formatString: 'dd-MM-yyyy hh:mm:ss',clearString: '${uiLabelMap.Clear}', todayString: '${uiLabelMap.Today}',showFooter:true});
		 	$('#thruDateAdd').jqxDateTimeInput({width: '200px',  allowNullDate : true,value : null,height: '25px', formatString: 'dd-MM-yyyy hh:mm:ss',clearString: '${uiLabelMap.Clear}', todayString: '${uiLabelMap.Today}',showFooter:true});
			$('#registrationDateAdd').jqxDateTimeInput({width: '200px', height: '25px', allowNullDate : true,value : null, formatString: 'dd-MM-yyyy hh:mm:ss',clearString: '${uiLabelMap.Clear}', todayString: '${uiLabelMap.Today}',showFooter:true});
		 	$('#registrationNumberAdd').jqxNumberInput({min : 0,max : 999999999999,digits : 15,decimalDigits : 0,spinButtons : true});
		 	$('#licenseNumberAdd').jqxNumberInput({min : 0,max : 999999999999,digits : 15,decimalDigits : 0,spinButtons : true});
		 	filterDate.init('fromDateAdd','thruDateAdd');
		 	$("#alterpopupWindow").jqxWindow({
				width: 500, height: 350, resizable: true,  isModal: true, autoOpen: false, cancelButton: $("#cancelCategory"), modalOpacity: 0.7, theme:theme           
			});
		 }
		 
		 var initRules = function(){
			$("#formAdd").jqxValidator({
					rules : [
						{input : '#fromDateAdd',message : '${StringUtil.wrapString(uiLabelMap.CommonRequired?default(''))}',action : 'change,close',
							rule : function(input,commit){
								var value = input.jqxDateTimeInput('val');
								if(!value) return false;
								return true;
							}
						},
					]
					});
				};	
		
		var save = function(){
			if(!$("#formAdd").jqxValidator('validate')){return;}
					var row;
			        row = {
			       		fixedAssetId : '${fixedAssetId?if_exists}',
			        	fromDate : $('#fromDateAdd').jqxDateTimeInput('getDate'),
			        	thruDate : $('#thruDateAdd').jqxDateTimeInput('getDate'),
			        	registrationDate : $('#registrationDateAdd').jqxDateTimeInput('getDate'),
			        	registrationNumber  : $('#registrationNumberAdd').jqxNumberInput('val'),
			        	licenseNumber : $('#licenseNumberAdd').jqxNumberInput('val'),
			        	govAgencyPartyId : $('input[name=govAgencyPartyIdAdd]').val()
			        	  };
				   $("#jqxgridRegistration").jqxGrid('addRow', null, row, "first");
		      return true;
		}
		
		var clear = function(){
			$('#fromDateAdd').jqxDateTimeInput('val',null);
	    	$('#thruDateAdd').jqxDateTimeInput('val',null);
	    	$('#registrationDateAdd').jqxDateTimeInput('val',null);
	    	$('#registrationNumberAdd').jqxNumberInput('clear');
	    	$('#licenseNumberAdd').jqxNumberInput('clear');
	    	$('input[name=govAgencyPartyIdAdd]').val('');
	    	filterDate.resetDate();
		}
		
		var bindEvent = function(){
			$("#saveCategory").click(function () {
				if(save()) $("#alterpopupWindow").jqxWindow('close');
			});
			$("#saveAndContinueCategory").click(function () {
				if(save()) clear();
			});
			
			$('#alterpopupWindow').on('close',function(){
				clear();
			})
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
		action.init();
	})
</script>	                     	