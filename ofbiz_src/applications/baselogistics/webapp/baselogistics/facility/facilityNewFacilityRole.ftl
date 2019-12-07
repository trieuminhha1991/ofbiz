<#include 'script/facilityNewFacilityRoleScript.ftl'/>
<form class="form-horizontal form-window-content-custom" id="initFacilityRole" name="initFacilityRole">
	<div class="row-fluid margin-top10">
		<div class="span12">
			<div class="span5">
				<div class="row-fluid margin-bottom10">	
					<div class="span4" style="text-align: right">
						<div class="asterisk"> ${uiLabelMap.Owner} </div>
					</div>
					<div class="span7">	
						<div id="ownerPartyId" style="width: 100%; margin-left: 0px !important" class="green-label asterisk"></div>
					</div>
				</div>
				<div class="row-fluid margin-bottom10">	
					<div class="span4" style="text-align: right">
						<div class="asterisk"> ${uiLabelMap.OwnFromDate} </div>
					</div>
					<div class="span8">	
						<div id="fromDate" style="width: 100%;" class="green-label asterisk"></div>
					</div>
				</div>
				<div class="row-fluid margin-bottom10">	
					<div class="span4" style="text-align: right">
						<div> ${uiLabelMap.OwnThruDate} </div>
					</div>
					<div class="span8">	
						<div id="thruDate" style="width: 100%;" class="green-label"></div>
					</div>
				</div>
			</div>
			<div class="span7">
				<div class="row-fluid margin-bottom10">	
					<div class="span4" style="text-align: right">
						<div> ${uiLabelMap.Storekeeper} </div>
					</div>
					<div class="span8">	
						<div id="storekeeperId" class="green-label">
							<div id="managerPartyId"></div>
							<div id="inputManagerPartyId" type="hidden"></div>
						</div>
					</div>
				</div>
				<div class="row-fluid margin-bottom10">	
					<div class="span4" style="text-align: right">
						<div> ${uiLabelMap.BLWorkFromDate} </div>
					</div>
					<div class="span8">	
						<div id="fromDateManager" style="width: 100%;" class="green-label asterisk"></div>
					</div>
				</div>
				<div class="row-fluid margin-bottom10">	
					<div class="span4" style="text-align: right">
						<div> ${uiLabelMap.BLWorkThruDate} </div>
					</div>
					<div class="span8">	
						<div id="thruDateManager" style="width: 100%;" class="green-label"></div>
					</div>
				</div>
			</div>
		</div>
	</div>
</form>