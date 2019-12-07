<div id="alterpopupWindow" class='hide'>
	<div>${uiLabelMap.createSupplier}</div>
	<div class='form-window-container'>
		<div class='form-window-content' style="height: 470px; overflow-y: auto; overflow-x: hidden">
			<div class="row-fluid">
				<div class="span4 align-right">${uiLabelMap.Year}:</div>
				<div class="span8">
					<input type="hidden" id="yearId" value="${year}"/>
					<b>${year}</b>
				</div>
			</div>
			<div class="row-fluid margin-bottom10">
				<div class="span4 align-right partyId">
					<#if planTypeId == "ORGANIZATION_PLAN">
						${uiLabelMap.groupName}:
					<#else>
						${uiLabelMap.Department}:
					</#if>
					
				</div>
				<div class="span8">
					<input type="hidden" id="departmentId" value="<#if planTypeId == "ORGANIZATION_PLAN">${companyId}<#else>${departmentId?if_exists}</#if>"/>
					<#if planTypeId == "ORGANIZATION_PLAN">
						<b>${companyName}</b>
					<#else>
						<b>${departmentName?if_exists}</b>
					</#if>
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class="span4 align-right">${uiLabelMap.estimatedBudget}</div>  
				<div class="span8">
					<div id="estimatedBudget"></div>
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class="span4 align-right asterisk">${uiLabelMap.currencyUomId}</div>  
				<div class="span8">
					<div id="currencyUomContainer"></div>
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class='span4 align-right asterisk'>
					${uiLabelMap.ProductPOProposal}
				</div>
				<div class='span8'>
					<div id="jqxgridProductChosen"></div>
				</div>
			</div>
			<div class='row-fluid'>
				<div class="span4 align-right asterisk">${uiLabelMap.Reason}</div>  
				<div class="span8">
					<div id="reasoncontainer">
						<textarea id="reason"></textarea>
					</div>
				</div>
			</div>
		</div>
		<div class="form-action">
			<div class='row-fluid'>
				<div class="span12 margin-top10">
					<button id="alterCancel" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.Cancel}</button>
					<!-- <button id="saveAndContinue" class='btn btn-success form-action-button pull-right'><i class='fa-plus'></i> ${uiLabelMap.SaveAndContinue}</button> -->
					<button id="alterSave" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.Save}</button>
				</div>
			</div>
		</div>
	</div>
</div>
