<div id="alterpopupWindow" class='hide'>
    <div>${uiLabelMap.accCreateNew}</div>
    <div style="overflow: hidden;">
    	<form class='form-horizontal'>
    		<div class='control-group no-left-margin'>
				<label for="insurancePaymentName" class="asterisk">${uiLabelMap.requirementStartDate}</label>  
				<div class="controls">
					<div id="requirementStartDate"></div>
				</div>
    		</div>
    		<div class='control-group no-left-margin'>
				<label for="insurancePaymentName" class="asterisk">${uiLabelMap.requiredByDate}</label>  
				<div class="controls">
					<div id="requiredByDate"></div>
				</div>
    		</div>
    		<div class='control-group no-left-margin'>
				<label for="insurancePaymentName">${uiLabelMap.estimatedBudget}</label>  
				<div class="controls">
					<div id="estimatedBudget"></div>
				</div>
    		</div>
    		<div class='control-group no-left-margin'>
				<label for="insurancePaymentName" class="asterisk">${uiLabelMap.currencyUomId}</label>  
				<div class="controls">
					<div id="currencyUomContainer"></div>
				</div>
    		</div>
    		<div class='control-group'>
    			<label class='asterisk'>
    				${uiLabelMap.ProductPOProposal}
    			</label>
    			<div class='controls'>
    				<div id="jqxgridProductChosen"></div>
    			</div>
    		</div>
    		<div class='control-group no-left-margin'>
				<label for="insurancePaymentName" class="asterisk">${uiLabelMap.Reason}</label>  
				<div class="controls">
					<div id="reasoncontainer">
						<textarea id="reason"></textarea>
					</div>
				</div>
    		</div>
    	</form>
		<div class="row-fluid wizard-actions pull-right">
			<button type="button" class='btn btn-primary btn-save' id="alterSave"><i class='fa fa-check'></i>&nbsp;${uiLabelMap.CommonSave}</button>
		</div>
    </div>
</div> 