<div class='form-window-container'>
	<div class='form-window-content'>
    	<div class="row-fluid">
    		<div class="span6">
				<div class="row-fluid margin-bottom10">	
    				<div class="span5" style="text-align: right">
    					<label class="asterisk"> ${uiLabelMap.LogRequirePurchaseForFacility}: </label>
					</div>
					<div class="span7">
						<div id="originFacilityId" style="width: 100%" class="green-label">
							<input id="testId"></input>
						</div>
					</div>
				</div>
				<div class="row-fluid margin-bottom10">	
    				<div class="span5" style="text-align: right">
    					<div> ${uiLabelMap.LogRequirePurchaseByDate}: </div>
					</div>
					<div class="span7">
						<div id="requirementByDate" style="width: 100%"></div>
					</div>
				</div>		
    		</div>
    		<div class="span6 no-left-margin">
    			<div class="row-fluid margin-bottom10">	
    				<div class="span5" style="text-align: right">
    					<div>${uiLabelMap.description}:</div>
					</div>
					<div class="span7">
						<input id="description"></input>
					</div>
				</div>
				<div class="row-fluid margin-bottom10">	
    				<div class="span5" style="text-align: right">
	    				<div> ${uiLabelMap.LogRequirePurchaseBeforeDate}: </div>
					</div>
						<div class="span7"> 
							<div id="requirementStartDate" style="width: 100%"></div>
						</div>
					</div>
	    		</div>
    		<div>
	    </div>
	</div>
</div>

<script>
	$("#description").jqxInput({placeHolder: ". . .", height: 20, width: '195'});
</script>
