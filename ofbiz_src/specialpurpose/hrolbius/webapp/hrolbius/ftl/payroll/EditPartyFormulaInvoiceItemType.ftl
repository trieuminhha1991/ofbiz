
<div id="${dataToggleModalId}" class="modal hide fade" tabindex="-1" >
	<div class="modal-header no-padding">
		<div class="table-header">
			<button type="button" class="close" data-dismiss="modal">&times;</button>
			${uiLabelMap.EditPartyFormulaInvoiceItemType}
		</div>
	</div>
	<div class="modal-body no-padding">	
		<div class="row-fluid">
			<form action="<@ofbizUrl>${linkUrl}</@ofbizUrl>" method="post" id="${formId}" name="EditPartyFormulaInvoiceItemType" class="basic-form form-horizontal">
				<div class="control-group no-left-margin">
					<label class="control-label asterisk">${uiLabelMap.formulaName}</label>
					<div class="controls">
						<select name="code" id="code">
							<#list formulaList as formula>
								<option value="${formula.code}">${formula.name}</option>
							</#list>
						</select>
					</div>
				</div>
				<div class="control-group no-left-margin">
					<label class="control-label asterisk">${uiLabelMap.AccountingInvoicePurchaseItemType}</label>
					<div class="controls">
						<select name="invoiceItemTypeId" id="invoiceItemTypeId" >
							<#list invoiceItemTypeList as invoiceItemType>	
								<option value="${invoiceItemType.invoiceItemTypeId}">${invoiceItemType.description}</option>
							</#list>
						</select>
					</div>
				</div>
				<div class="control-group no-left-margin">
					<label class="control-label asterisk">${uiLabelMap.Department}</label>
					<div class="controls">
						<select name="partyListId" id="partyListId" multiple="multiple" class="chzn-select">
							<#list directChildDepartment as department>
								<#assign partyName = Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, department.partyId, false)>
								<option value="${department.partyId}" <#if parameters.departmentList?exists && parameters.departmentList?contains(department.partyId)>selected="selected"</#if>>${partyName}</option>
							</#list>
						</select>
					</div>
				</div>
				<div class="control-group no-left-margin">
					<label class="control-label">${uiLabelMap.AvailableFromDate}</label>
					<div class="controls">
						<@htmlTemplate.renderDateTimeField name="fromDate" event="" action="" value="" className="" alert="" title="Format: yyyy-MM-dd HH:mm:ss.SSS" size="25" maxlength="30" id="fromDate" dateType="date" shortDateInput=true timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
					</div>
				</div>
				<div class="control-group no-left-margin">
					<label class="control-label">&nbsp;</label>
					<div class="controls">
						<button class="btn btn-primary btn-small" type="submit">
			 				<i class = "icon-ok" ></i>
		     				${uiLabelMap.CommonSubmit}
		 				</button>
					</div>
				</div>
			</form>
		</div>
	</div>
</div>
<script type="text/javascript">
jQuery(document).ready(function() {
	jQuery("#${createNewLinkId}").attr("data-toggle", "modal");
	jQuery("#${createNewLinkId}").attr("role", "button");
	jQuery("#${createNewLinkId}").attr("href", "#${dataToggleModalId}");
	
	$('#EditPartyFormulaIIT').validate({
		errorElement: 'span',
		errorClass: 'help-inline red-color',
		errorPlacement: function(error, element) {
			element.addClass("border-error");
    		if (element.parent() != null ){   
				element.parent().find("button").addClass("button-border");     			
    			error.appendTo(element.parent());
			}
    	  },
    	unhighlight: function(element, errorClass) {
    		$(element).removeClass("border-error");
    		$(element).parent().find("button").removeClass("button-border");
    	},
		focusInvalid: false,
		rules: {
			partyListId: {
				required: true,
			}
			
		},

		messages: {
			partyListId: {
				required: "<span style='color:red;'>${uiLabelMap.CommonRequired}</span>",
			},			
		},

		invalidHandler: function (event, validator) { //display error alert on form submit   
			$('.alert-error', $('.login-form')).show();
		},

		highlight: function (e) {
			$(e).closest('.control-group').removeClass('info').addClass('error');
		},

		success: function (e) {
			$(e).closest('.control-group').removeClass('error').addClass('info');
			$(e).remove();
		},

		submitHandler: function (form) {
			form.submit();
		},
		invalidHandler: function (form) {
		}
		
	});
});
</script>