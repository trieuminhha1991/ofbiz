<#include 'script/physicalInvDetailInfoScript.ftl'/>
<div id="general-tab" class="tab-pane<#if activeTab?exists && activeTab == "general-tab"> active</#if>">
<div class="form-horizontal form-window-content-custom label-text-left content-description" style="margin:10px">
	<div class="row-fluid">
		<div class="span11">
			<div class='row-fluid' style="margin-bottom: -10px !important">
				<div class="span6">
					<div class='row-fluid'>
						<div class='span5'>
							<span style="float: right;">${uiLabelMap.PhysicalInventoryId}</span>
						</div>
						<div class="span7">
							<div class="green-label" id="physicalInventoryId" name="physicalInventoryId"></div>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<span style="float: right;">${uiLabelMap.Facility}</span>
						</div>
						<div class="span7">
							<div class="green-label" id="facilityId" name="facilityId"></div>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<span style="float: right;">${uiLabelMap.PhysicalInventoryDate}</span>
						</div>
						<div class="span7">
							<div class="green-label" id="physicalInventoryDate" name="physicalInventoryDate"></div>
				   		</div>
			   		</div>
				</div>
				<div class="span6">
					<div class='row-fluid'>
						<div class='span5'>
							<span style="float: right;">${uiLabelMap.PartyExecuted}</span>
						</div>
						<div class="span7">
							<div class="green-label" id="partyId"></div>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<span style="float: right;">${uiLabelMap.Description}</span>
						</div>
						<div class="span7">
							<div class="green-label" id="generalComments" name="generalComments"></div>
				   		</div>
					</div>
				</div>
			</div>
		</div><!--.span11-->
	</div><!--.row-fluid-->
</div>
	<#include "physicalInventoryTotal.ftl"/>
</div>