<@jqGridMinimumLib/>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<div class="row-fluid">
	<form action="<@ofbizUrl>createRequestDisciplineProposal</@ofbizUrl>" id="${formName}" name="${formName}" method="post" class="basic-form form-horizontal">
		<div class="control-group no-left-margin">
			<label class="control-label asterisk">
				${uiLabelMap.EmployeeName}
			</label>
			<div class="controls">
				<#if employee?exists>
					${employee.lastName?if_exists} ${employee.middleName?if_exists} ${employee.firstName?if_exists} [${employee.partyId}]
					<input type="hidden" value="${employee.partyId}" name="partyId">
				<#else>	
					<select id="${formName}_partyId" name="partyId" class="chzn-select">
						<option></option>
						<#list emplList as empl>
							<option value="${empl.partyId}">${empl.lastName?if_exists} ${empl.middleName?if_exists} ${empl.firstName?if_exists}</option>
						</#list>
					</select>
				</#if>
			</div>
		</div>
		<div class="control-group no-left-margin">
			<label class="control-label">
				${uiLabelMap.HREmplFromPositionType}
			</label>
			<div class="controls">
				<span class="currPosition">
					${currPositionsStr?if_exists}&nbsp;
				</span>
			</div>
		</div>
		<div class="control-group no-left-margin">
			<label class="control-label">
				${uiLabelMap.EmployeeCurrentDept}
			</label>
			<div class="controls">
				<span class="currDept">
					${currDept?if_exists}&nbsp;
				</span>
			</div>
		</div>
		<div class="control-group no-left-margin">
			<label class="control-label">
				${uiLabelMap.HREmplWarningLevel}
			</label>
			<div class="controls">
				<span class="punishmentCountSum">
					<#if partyPunishmentRemindCount?exists && partyPunishmentRemindCount.punishmentCountSum?exists>
						${partyPunishmentRemindCount.punishmentCountSum}
					<#else>
						0	
					</#if>
					&nbsp;
				</span>
			</div>
		</div>
		<div class="control-group no-left-margin">
			<label class="control-label">
				${uiLabelMap.HREmplPunishmentLevel}
			</label>
			<div class="controls">
				<span class="punishmentLevel">
					<#if partyPunishmentLevel?exists && partyPunishmentLevel.punishmentLevel?exists>
						${partyPunishmentLevel.punishmentLevel}
					<#else>
						0	
					</#if> 
				</span>
			</div>
		</div>
		<div class="control-group no-left-margin">
			<label class="control-label asterisk">
				${uiLabelMap.HRCommonHandlerProposal}
			</label>
			<div class="controls">
				<#assign listPartyApproverProposal = Static["com.olbius.util.PartyUtil"].buildOrg(delegator, "DHR").getEmployeeInOrg(delegator)>
				<@htmlTemplate.renderComboxBox name="approverIdProposal" id="approverIdProposal" emplData=listPartyApproverProposal 
					container="jqxComboBox1" multiSelect="false"/>
			</div>
		</div>
		<div class="control-group no-left-margin">
			<label class="control-label">
				${uiLabelMap.HRNotes}
			</label>
			<div class="controls">
				<input type="text" name="commnent" value="">
			</div>
		</div>
		<div class="control-group no-left-margin">
			<label class="control-label">
				&nbsp;
			</label>
			<div class="controls">
				<button type="button" class="btn btn-small btn-primary" name="submitButton" id="btnSubmit"
					><i class="icon-ok"></i>
					${uiLabelMap.CommonSubmit}	
				</button>
			</div>
		</div>
	</form>
</div>
<script type="text/javascript">

jQuery(document).ready( function() {
	jQuery("#btnSubmit").click(function(){
		//console.log($('#EditPayrollTable').jqxValidator('validate'));
		if(!validForm.valid() || !$('#${formName}').jqxValidator('validate')){
			return false;
		}
		if(!confirm('${StringUtil.wrapString(uiLabelMap.AreYouSure)}')){
			return false;
		}
		$('#${formName}').submit();
	});
	
	$('#${formName}').jqxValidator({
		 rules: [{
	          input: '#jqxComboBox1',
	          message: '${uiLabelMap.CommonRequired}',
	          action: 'change',
	          rule: function () {
	              var item = $("#jqxComboBox1").jqxComboBox('getSelectedItem');
	              if (!item) return false;
	              return true;
	          }
	      },
	      ]
	});
	var validForm = $('#${formName}').validate({
		errorElement: 'span',
		errorClass: 'help-inline red-color',
		focusInvalid: false,
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
		rules:{
			partyId:{
				required:true
			}
		},
		messages:{
			partyId:{
				required:"${StringUtil.wrapString(uiLabelMap.CommonRequired)}"
			}
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