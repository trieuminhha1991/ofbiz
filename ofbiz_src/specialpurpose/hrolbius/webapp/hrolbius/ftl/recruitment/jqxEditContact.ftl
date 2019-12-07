<div class="basic-form form-horizontal" style="margin-top: 10px">
	<form name="createNewContact" id="createNewContact">	
		<div class="row-fluid" >
    		<div class="span12">
    			<div class="span6">
    				<div class="control-group no-left-margin">
						<label class="control-label">${uiLabelMap.homeTel}:</label>
						<div class="controls">
							<input type="text" name="homeTel" id="homeTel">
						</div>
					</div>
					<div class="control-group no-left-margin">
						<label class="control-label">${uiLabelMap.diffTel}:</label>
						<div class="controls">
							<input type="text" name="diffTel" id="diffTel">
						</div>
					</div>
				</div>
				<div class="span6">
					<div class="control-group no-left-margin">
						<label class="control-label asterisk">${uiLabelMap.mobile}:</label>
						<div class="controls">
							<input type="text" name="mobile" id="mobile">
						</div>
					</div>
					<div class="control-group no-left-margin">
						<label class="control-label">${uiLabelMap.email}:</label>
						<div class="controls">
							<input id="email"></input>
						</div>
					</div>
				</div>
			</div>
			<div class="span12" style="margin-top: 50px; margin-left: 0px;">
				<div class="span5" style="border: 1px solid #EEE; padding: 15px; border-radius: 5px; margin-left: 40px;">
					<div class="title-border">
						<span>${uiLabelMap.PermanentResidence}</span>
					</div>
					<div class="control-group no-left-margin">
						<label class="control-label asterisk" style="width: 104px !important">${uiLabelMap.PartyAddressLine}:</label>
						<div class="controls" style="margin-left: 150px !important">
							<input type="text" name="prAddress" id="prAddress">
						</div>
					</div>
					<div class="control-group no-left-margin">
						<label class="control-label" style="width: 100px !important">${uiLabelMap.CommonCountry}:</label>
						<div class="controls" style="margin-left: 150px !important">
							<div id="prCountry"></div>
						</div>
					</div>
					<div class="control-group no-left-margin">
						<label class="control-label" style="width: 100px !important">${uiLabelMap.PartyState}:</label>
						<div class="controls" style="margin-left: 150px !important">
							<div id="prProvince"></div>
						</div>
					</div>
					<div class="control-group no-left-margin">
						<label class="control-label" style="width: 100px !important">${uiLabelMap.PartyDistrictGeoId}:</label>
						<div class="controls" style="margin-left: 150px !important">
							<div id="prDistrict"></div>
						</div>
					</div>
					<div class="control-group no-left-margin">
						<label class="control-label" style="width: 100px !important">${uiLabelMap.PartyWardGeoId}:</label>
						<div class="controls" style="margin-left: 150px !important">
							<div id="prWard"></div>
						</div>
					</div>
				</div>
				<div style="width:50px;float:left; margin-left:30px; margin-top: 100px">
					<div style="margin-bottom: 5px">
						<button type="button" class="btn btn-mini btn-success copy-back" ><i class="icon-arrow-left"></i></button>
					</div>
					<div>
						<button type="button" class="btn btn-mini btn-primary copy-next" ><i class="icon-arrow-right"></i></button>
					</div>
				</div>
				<div class="span5" style="border: 1px solid #EEE; padding: 15px; border-radius: 5px; margin-left: 10px;">
					<div class="title-border">
						<span>${uiLabelMap.CurrentResidence}</span>
					</div>
					<div class="control-group no-left-margin">
						<label class="control-label asterisk" style="width: 104px !important">${uiLabelMap.PartyAddressLine}:</label>
						<div class="controls" style="margin-left: 150px !important">
							<input id="crAddress">
						</div>
					</div>
					<div class="control-group no-left-margin">
						<label class="control-label" style="width: 100px !important">${uiLabelMap.CommonCountry}:</label>
						<div class="controls" style="margin-left: 150px !important">
							<div id="crCountry"></div>
						</div>
					</div>
					<div class="control-group no-left-margin">
						<label class="control-label" style="width: 100px !important">${uiLabelMap.PartyState}:</label>
						<div class="controls" style="margin-left: 150px !important">
							<div id="crProvince"></div>
						</div>
					</div>
					<div class="control-group no-left-margin">
						<label class="control-label" style="width: 100px !important">${uiLabelMap.PartyDistrictGeoId}:</label>
						<div class="controls" style="margin-left: 150px !important">
							<div id="crDistrict"></div>
						</div>
					</div>
					<div class="control-group no-left-margin">
						<label class="control-label" style="width: 100px !important">${uiLabelMap.PartyWardGeoId}:</label>
						<div class="controls" style="margin-left: 150px !important">
							<div id="crWard"></div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</form>
</div>