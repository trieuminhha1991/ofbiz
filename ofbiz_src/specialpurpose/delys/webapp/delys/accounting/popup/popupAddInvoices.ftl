<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>	
<style>
	.ui-autocomplete.ui-menu.ui-widget.ui-widget-content.ui-corner-all {
		z-index:18005!important;
	}
	.view-calendar input,.field-lookup input {
		width:176px;
	}
</style>	 

<div id="alterpopupWindow" style="display :none;">
	<#if typeIV?exists && typeIV == "AR">
		<div>${uiLabelMap.AccountingCreateNewSalesInvoice}</div>
	<#elseif typeIV == "AP">
		<div>${uiLabelMap.AccountingCreateNewPurchaseInvoice}</div>
	</#if>
	
	<div style="overflow: hidden;">
		<div class='form-window-container'>
			<div class='form-window-content'>
			<form name="AddInvoices">	
				<div class='row-fluid margin-bottom10'>
					<div class='span5 text-algin-right'>
						<label class="asterisk">${uiLabelMap.FormFieldTitle_invoiceTypeId}</label>
					</div>  
					<div class="span7">
						<div id="invoiceTypeIdShow"></div>
			   		</div>		
				</div>
				<div class='row-fluid margin-bottom10'>
			   		<div class='span5 text-algin-right'>
						<label class="asterisk">${uiLabelMap.CompanyAndUnit}</label>
					</div>  
					<div class="span7">
						<div id="organizationPartyIdShow"></div>
			   		</div>
			   	</div>
			   	<div class='row-fluid margin-bottom10'>
			   		<div class='span5 text-algin-right'>
			   			<#if typeIV?exists && typeIV == "AR">
			   				<label class="asterisk">${uiLabelMap.Customer}</label>
			   			<#elseif typeIV == "AP">
			   				<label class="asterisk">${uiLabelMap.SupplierAndEmployee}</label>
			   			</#if>
						
					</div>  
					<div class="span7">
						<#if typeIV?exists && typeIV == "AR">
							<@htmlTemplate.lookupField name="partyIdFromdShow" id="partyIdFromdShow" value='' width="900" height="600" zIndex="18005"
								formName="AddInvoices" fieldFormName="LookupJQPartyName" title="${uiLabelMap.CommonSearch} ${uiLabelMap.Customer}" />
			   			<#elseif typeIV == "AP">
			   				<@htmlTemplate.lookupField name="partyIdFromdShow" id="partyIdFromdShow" value='' width="900" height="600" zIndex="18005"
								formName="AddInvoices" fieldFormName="LookupJQPartyName" title="${uiLabelMap.CommonSearch} ${uiLabelMap.SupplierAndEmployee}" />
			   			</#if>
					</div>
			   	</div>
			
			</div>
			 </form>
			<div class="form-action">
				<button id="cancel" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.Cancel}</button>
				<button id="save" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.Save}</button>
			</div>
		</div>
	</div>
	
</div>		

<script>
 $.jqx.theme = 'olbius';
    var theme = $.jqx.theme;
 <#assign organList= delegator.findList("PartyAcctgPrefAndGroup",null,null,null,null,false)/>
    var organSource=new Array();
    <#if organList?has_content>
        <#list organList as orItem>
            var rowOran= {};
            rowOran['partyId']= "${orItem.partyId}";
            rowOran['groupName']="${orItem.groupName}";
            organSource[${orItem_index}]=rowOran;
        </#list>
    </#if>
    
    
	var action = (function(){
		var initElement = function(){
			$("#alterpopupWindow").jqxWindow({theme : theme,width : '470',height : '250',cancelButton : $('#cancel'),isModal : true,draggable : false,  autoOpen: false,modalOpacity: 0.7 });
		    $("#invoiceTypeIdShow").jqxDropDownList({filterable: true,source: dataInvoiceType, width: 217 ,autoDropDownHeight : true, displayMember:"description" ,valueMember: "invoiceTypeId",placeHolder : '${StringUtil.wrapString(uiLabelMap.PleaseChooseAcc?default(''))}'});
		  	 $("#organizationPartyIdShow").jqxDropDownList({source: organSource, width: 217, displayMember:"groupName",autoDropDownHeight : true ,valueMember: "partyId",placeHolder : '${StringUtil.wrapString(uiLabelMap.PleaseChooseAcc?default(''))}'});
		}
		
	var bindEvent = function(){
			$('#alterpopupWindow').bind('keypress',function(e){if(e.keyCode == 13)
			   {
			      return false;
			   }})
				
			$('#0_lookupId_partyIdFromdShow').bind('lookupIdChange',function(){
					$("form[name=AddInvoices]").jqxValidator('hideHint','input[name=partyIdFromdShow]');
				})
				
			$('#save').click(function(){
				if(addInvoices()){$('#alterpopupWindow').jqxWindow('close')};
			})
			
			$('#alterpopupWindow').bind('close',function(){
				clear();
			})	
		}	
		
		 var initRule = function(){
				$("form[name=AddInvoices]").jqxValidator({
			        	rules: [{
			        	input: "#invoiceTypeIdShow", message: "${uiLabelMap.CommonRequired}", action: 'blur,change', 
			        	rule: function (input, commit) {
			                var index = input.jqxDropDownList('getSelectedIndex');
			                return index != -1;
			            }
			   		},{
			        	input: "#organizationPartyIdShow", message: "${uiLabelMap.CommonRequired}", action: 'blur,change', 
			        	rule: function (input, commit) {
			                var index = input.jqxDropDownList('getSelectedIndex');
			                return index != -1;
			            }
			   		},{
			        	input: "input[name=partyIdFromdShow]", message: "${uiLabelMap.CommonRequired}", action: 'change,close', 
			        	rule: function (input, commit) {
			                if($("input[name=partyIdFromdShow]").val()){
			                	return true;
			                }
			                return false;
			            }
			   		}]
			    });
			};
		
		var addInvoices = function(){
					if(!$("form[name=AddInvoices]").jqxValidator('validate')){return false;}
					var row = {
						statusId: "INVOICE_IN_PROCESS",
						currencyUomId:"${defaultOrganizationPartyCurrencyUomId?if_exists}",
						invoiceTypeId: $("#invoiceTypeIdShow").jqxDropDownList('val'),
						partyIdFrom:$('#organizationPartyIdShow').jqxDropDownList('val') ? $('#organizationPartyIdShow').jqxDropDownList('val') : null,
						partyId:$('#0_lookupId_partyIdFromdShow').val() ? $('#0_lookupId_partyIdFromdShow').val() : null,
						partyIdFromdShow:$('#0_lookupId_partyIdFromdShow').val() ? $('#0_lookupId_partyIdFromdShow').val() : null
						
					}
					postData(row);
					return true;
				}
				
				var postData = function(row){
					if(row){
						$.ajax({
							<#if typeIV?exists && typeIV == "AR">
								url : 'accArcreateInvoiceJSON',
				   			<#elseif typeIV == "AP">
				   				url : 'accApcreateInvoiceJSON',
				   			</#if>
							data : row,
							datatype : 'json',
							async : false,
							type : 'POST',
							success : function(response){
								if(response._ERROR_MESSAGE_LIST_ || response._ERROR_MESSAGE_){
									$('#containerjqxgrid').empty();
			                        $('#jqxNotificationjqxgrid').jqxNotification({ template: 'error'});
			                        $("#notificationContentjqxgrid").text(response._ERROR_MESSAGE_LIST_ ? response._ERROR_MESSAGE_LIST_  :  response._ERROR_MESSAGE_);
			                        $("#jqxNotificationjqxgrid").jqxNotification("open");
								}else{
									if(response.invoiceId){
										<#if typeIV?exists && typeIV == "AR">
										window.location.href = "accAreditInvoice?invoiceId=" + response.invoiceId;
							   			<#elseif typeIV == "AP">
							   			window.location.href = "accApeditInvoice?invoiceId=" + response.invoiceId;
							   			</#if>
									}
								}
							},
							error : function(){
								
							}
						})
					}
				}
		
			var clear = function(){
					$("#invoiceTypeIdShow").jqxDropDownList('clearSelection');
					$('#organizationPartyIdShow').jqxDropDownList('clearSelection');
					$('#0_lookupId_partyIdFromdShow').val('');
					$("form[name=AddInvoices]").jqxValidator('hide');
				}
					
			return {
				init : function(){
					initElement();
					initRule();
					bindEvent();
				}
			}
	}())
   
   
    $(document).ready(function(){
    	action.init();
    })

</script> 