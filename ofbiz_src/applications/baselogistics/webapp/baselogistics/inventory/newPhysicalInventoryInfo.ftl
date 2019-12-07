<#include 'script/newPhysicalInvInfoScript.ftl'/>
<form class="form-horizontal form-window-content-custom" id="initPhyscialInv" name="initPhyscialInv" method="post" action="">
	<div class="row-fluid" style="margin-bottom: 0px !important">
		<div class="span12">
			<div class='row-fluid' style="margin-bottom: 0px !important">
				<div class="span5">
					<div class='row-fluid'>
						<div class='span5'>
							<span class="asterisk">${uiLabelMap.Facility}</span>
						</div>
						<div class="span7">
							<div class="green-label" id="facilityId" name="facilityId"></div>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<span class="asterisk">${uiLabelMap.PartyExecuted}</span>
						</div>
						<div class="span7">
							<div class="green-label" id="partyId" name="partyId"></div>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'> 
							<span class="asterisk">${uiLabelMap.PhysicalInventoryDate}</span>
						</div>
						<div class="span7">
							<div id="physicalInventoryDate"></div>
				   		</div>
					</div>
				</div>
				<div class="span7">
					<div class='row-fluid'>
						<div class='span4'>
							<span>${uiLabelMap.Description}</span>
						</div>
						<div class="span8" style="text-align: left">
							<textarea id="generalComments" name="generalComments" data-maxlength="250" rows="2" style="resize: vertical;margin-top: 0px" class="span12"></textarea>
				   		</div>
					</div>
				</div>
			</div>
		</div><!--.span11-->
	</div><!--.row-fluid-->
</form>