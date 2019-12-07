<div>
	${uiLabelMap.PageTitleEditFacilityContent}
</div>		
<div class='form-window-container'>
		<div class='form-window-content'>
	        <div class="row-fluid">
	    		<div class="span6">
	    			<div class="row-fluid">	
	    				<div class="span5" style="text-align: right">
	    					<div> ${uiLabelMap.Facility}: </div>
    					</div>
    					<div class="span7">	
    						<div id="facilityId" style="width: 100%;" class="green-label"></div>
						</div>
					</div>
					<div class="row-fluid">	
	    				<div class="span5" style="text-align: right">
	    					<div> ${uiLabelMap.FacilityAddress}: </div>
						</div>
						<div class="span7">	
							<div id="contactMechId" style="width: 100%;" class="green-label"></div>
						</div>
					</div>
					<div class="row-fluid">	
						<div class="span5" style="text-align: right">
	    					<div> ${uiLabelMap.ShipBeforeDate}: </div>
    					</div>
    					<div class="span7">	
    						<div id="transferBeforeDate" style="width: 100%;"></div>
    					</div>
    				</div>
	    		</div>
	    		<div class="span6 no-left-margin">
		    		<div class="row-fluid">	
						<div class="span5" style="text-align: right">
						</div>
						<div class="span7">	
						</div>
					</div>
		    		<div class="row-fluid">	
						<div class="span5" style="text-align: right">
							<div>${uiLabelMap.description}:</div>
						</div>
						<div class="span7">	
							<div style="width: 195px; display: inline-block; margin-bottom:3px"><input id="descChannel"></input></div><a onclick="showEditor()" style="display: inline-block, padding-left: 10px"><i style="padding-left: 10px;" class="icon-edit"></i></a>
						</div>
					</div>
					<div class="row-fluid">	
						<div class="span5" style="text-align: right">
							<div>${uiLabelMap.ShipAfterDate}:</div>
						</div>
						<div class="span7">	
							<div id="transferAfterDate" style="width: 100%;"></div>
						</div>
					</div>
	    		</div>
	    		<div>
	    		    <div style="margin: 20px 0px 10px 20px !important;"><div id="jqxgridProductChannel"></div></div>
	    	    </div>
		    </div>
    	</div>
    	<div class="form-action">
	        <div class='row-fluid'>
	            <div class="span12 margin-top20" style="margin-bottom:10px;">
	                <button id="addChannelCancel" class='btn btn-danger form-action-button pull-right' style="margin-right:20px !important;"><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
	                <button id="addChannelSave" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
	            </div>
	        </div>
	    </div>
	</div>
