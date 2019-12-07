<div class="row-fluid">
	<#if !suspendInsuranceReason?exists>
		<#assign suspendInsuranceReason = delegator.makeValue("SuspendParticipateInsuranceReason")>
	</#if>
	<div class="span12">
		<div class="widget-box transparent no-bottom-border">
			<div id="" class="widget-body">
				<#if suspendInsuranceReason.suspendReasonId?exists>
					<#assign url = "updateSuspendInsuranceReason">
				<#else>	
					<#assign url = "createSuspendInsuranceReason">
				</#if>
				<form action="<@ofbizUrl>${url}</@ofbizUrl>" class="basic-form form-horizontal" method="post" id="createSuspendInsuranceReason">								
					<div class="row-fluid">
						<div class="">
							<div class="control-group no-left-margin ">
								<label class="">
									<label for="EditFormula_code" class="asterisk" id="">${uiLabelMap.SuspendReasonId}</label>  
								</label>
								<div class="controls">
									<#if suspendInsuranceReason.suspendReasonId?exists>
										${suspendInsuranceReason.suspendReasonId}
										<input type="hidden" name="suspendReasonId" value="${suspendInsuranceReason.suspendReasonId}">
									<#else>	
										<input type="text" name="suspendReasonId">
									</#if>
									 
							 	</div>
							</div>
							<div class="control-group no-left-margin ">
								<label class="">
									<label for="EditFormula_code" class="asterisk" id="">${uiLabelMap.CommonDescription}</label>  
								</label>
								<div class="controls">
									<input type="text" name="description" value="${suspendInsuranceReason.description?if_exists}">
								</div>
							</div>
							<div class="control-group no-left-margin">
							    <label class="">
							    	<label for="EditFormula_name" class="asterisk" id="">${uiLabelMap.SuspendInsuranceParticipateType}</label>  
						    	</label>
							    <div class="controls">
									<select name="insuranceParticipateTypeId">
										<#list insuranceParticipateType as type>
											<option value="${type.insuranceParticipateTypeId}" <#if suspendInsuranceReason.insuranceParticipateType == type.insuranceParticipateTypeId>selected="selected"</#if>>${type.description}</option>
										</#list>
									</select>  
							    </div>
						    </div>
						    <div class="control-group no-left-margin">
							    <label class="">
							    	<label for="EditFormula_name"  id="">${uiLabelMap.FunctionCalcBenefit}</label>  
						    	</label>
							    <div class="controls">
									<select name="functionCalcBenefit" id="functionCalcBenefit" onchange="getFunction('functionCalcBenefit')">
										<option></option>	
										<#list insuranceFormulaList as formula>
											<option value="${formula.code}" <#if suspendInsuranceReason.functionCalcBenefit == formula.code>selected="selected"</#if>>${formula.description?if_exists}</option>
										</#list>
									</select>  
							    </div>
						    </div>
						    <div class="control-group no-left-margin">
						    	<label class="">
							    	<label for="EditFormula_name"id="">${uiLabelMap.FunctionFormula}</label>  
						    	</label>
						    	<div class="controls">
									<div id="function">
										&nbsp;
									</div>  
							    </div>
						    </div>
						    <div class="control-group no-left-margin">
								<label>
									&nbsp;
								</label>
								<div class="controls">
									<button type="submit" class="btn btn-small btn-primary icon-ok">
										${uiLabelMap.CommonSubmit}
									</button>
								</div>	
							</div>
						</div>						
				  	</div>
				</form>
			</div>
		</div>
	</div>
</div>
<script type="text/javascript">
function getFunction(selectId){
	var code = jQuery("#" + selectId).val();	
	jQuery.ajax({
		url: "<@ofbizUrl>getFunctionFormula</@ofbizUrl>",
		data: {code: code},
		type: 'POST',
		success: function(data){
			if(data.functionStr){
				jQuery("#function").empty();
				jQuery("#function").html(data.functionStr);
			}
		}
	});
}
$(function(){
	
});
</script>