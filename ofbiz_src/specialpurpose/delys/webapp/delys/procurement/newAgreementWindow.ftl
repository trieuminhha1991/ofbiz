<div id="alterpopupWindow" style="display:none;">
    <div>${uiLabelMap.accCreateNew}</div>
    <div style="overflow: hidden;">
    	<input type="hidden" name="statusIdAdd" id="statusIdAdd" value="AGREEMENT_CREATED"/>
    	<div class='row-fluid form-window-content'>
    		<div class='span6'>
    			<div class='row-fluid margin-bottom10'>
    				<div class='span5 align-right'>
    					${uiLabelMap.DAProductId}:
    				</div>
    				<div class='span7'>
    					<div id="productIdAdd">
		 					<div id="jqxProductGrid"></div>
		 				</div>
    				</div>
    			</div>
    			<div class='row-fluid margin-bottom10'>
    				<div class='span5 align-right'>
    					${uiLabelMap.DAPartyFrom}:
    				</div>
    				<div class='span7'>
    					<div id="partyIdFromAdd">
		 					<div id="jqxPartyFromGrid" ></div>
		 				</div>
    				</div>
    			</div>
    			<div class='row-fluid margin-bottom10'>
    				<div class='span5 align-right'>
    					${uiLabelMap.DAPartyTo}:
    				</div>
    				<div class='span7'>
    					<div id="partyIdToAdd">
		 					<div id="jqxPartyToGrid"></div>
		 				</div>
    				</div>
    			</div>
    			<div class='row-fluid margin-bottom10'>
    				<div class='span5 align-right'>
    					${uiLabelMap.DARoleTypeIdFrom}:
    				</div>
    				<div class='span7'>
    					<div id="roleTypeIdFromAdd"></div>
    				</div>
    			</div>
    			<div class='row-fluid margin-bottom10'>
    				<div class='span5 align-right'>
    					${uiLabelMap.DARoleTypeIdTo}:
    				</div>
    				<div class='span7'>
    					<div id="roleTypeIdToAdd"></div>
    				</div>
    			</div>
    			<div class='row-fluid margin-bottom10'>
    				<div class='span5 align-right'>
    					${uiLabelMap.DAAgreementTypeId}:
    				</div>
    				<div class='span7'>
    					<div id="agreementTypeIdAdd"></div>
    				</div>
    			</div>
    		</div>
    		<div class='span5'>
    			<div class='row-fluid margin-bottom10'>
    				<div class='span5 align-right'>
    					${uiLabelMap.DAAgreementDate}:
    				</div>
    				<div class='span7'>
    					<div id="agreementDateAdd"></div>
    				</div>
    			</div>
    			<div class='row-fluid margin-bottom10'>
    				<div class='span5 align-right'>
    					${uiLabelMap.DAFromDate}:
    				</div>
    				<div class='span7'>
    					<div id="fromDateAdd"></div>
    				</div>
    			</div>
    			<div class='row-fluid margin-bottom10'>
    				<div class='span5 align-right'>
    					${uiLabelMap.DAThruDate}:
    				</div>
    				<div class='span7'>
    					<div id="thruDateAdd"></div>
    				</div>
    			</div>
    			<div class='row-fluid margin-bottom10'>
    				<div class='span5 align-right'>
    					${uiLabelMap.DADescription}:
    				</div>
    				<div class='span7'>
    					<input id="descriptionAdd"/>
    				</div>
    			</div>
    			<div class='row-fluid margin-bottom10'>
    				<div class='span5 align-right'>
    					${uiLabelMap.textValue}:
    				</div>
    				<div class='span7'>
    					<input id="textDataAdd"/>
    				</div>
    			</div>
    		</div>
    	</div>
    	<div class="form-action">
			<div class='row-fluid'>
				<div class="span12 margin-top10">
					<button id="alterCancel" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.Cancel}</button>
					<button id="alterSave" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.Save}</button>
				</div>
			</div>
		</div>
    </div>
</div>