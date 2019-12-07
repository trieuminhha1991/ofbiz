<#include 'script/newPhysicalInventoryConfirmScript.ftl'/>
<div style="position:relative">
	<div class="row-fluid">
		<div class="form-horizontal form-window-content-custom label-text-left content-description" style="margin:10px">
			<div class="span11">
				<div class='row-fluid' style="margin-bottom: -10px !important">
					<div class="span6">
						<div class='row-fluid'>
							<div class='span5'>
								<span>${uiLabelMap.Facility}</span>
							</div>
							<div class="span7">
								<div class="green-label" style="float: left;" id="facilityIdDT" name="facilityIdDT"></div>
					   		</div>
						</div>
						<div class='row-fluid'>
							<div class='span5'>
								<span>${uiLabelMap.PartyExecuted}</span>
							</div>
							<div class="span7">
								<div class="green-label" id="partyIdDT" style="float: left;" name="partyIdDT"></div>
					   		</div>
						</div>
					</div>
					<div class="span6">
						<div class='row-fluid'>
							<div class='span5'>
								<span>${uiLabelMap.PhysicalInventoryDate}</span>
							</div>
							<div class="span7">
								<div id="physicalInventoryDateDT" class="green-label" style="float: left;"></div>
					   		</div>
						</div>
						<div class='row-fluid'>
							<div class='span5'>
								<span>${uiLabelMap.Description}</span>
							</div>
							<div class="span7">
								<div id="generalCommentsDT" class="green-label"></div>
					   		</div>
						</div>
					</div>
				</div>
			</div>
		</div><!--.span11-->
	</div><!--.row-fluid-->
</div>
<div class="row-fluid margin-top20">
	<div class="span12">
		<div class="tabbable">
			<ul class="nav nav-tabs" id="recent-tab">
				<li class="active" id="tabTotal">
					<a data-toggle="tab" href="#groupByProduct" title="${uiLabelMap.InventoryCounted}>">
						<span>${uiLabelMap.InventoryCounted}</span>
					</a>
				</li>
				<li class="" id="tabUpdate">
					<a data-toggle="tab" href="#itemToUpdate" title="${uiLabelMap.InventoryUpdated}">
						<span>${uiLabelMap.InventoryUpdated}</span>
					</a>
				</li>
				<li class="" id="tabVariance">
					<a data-toggle="tab" href="#detailItems" title="${uiLabelMap.InventoryVariance}">
						<span>${uiLabelMap.InventoryVariance}</span>
					</a>
				</li>
			</ul>
		</div><!--.tabbable-->
	</div>
</div>
<div class="tab-content overflow-visible" style="padding:0; border: none !important;">
	<div class="tab-pane active" id="groupByProduct">
		<div class="span12 margin-top10 margin-bottom10">
			<div id="jqxgridInvGroupByProduct" style="width: 100%"></div>
		</div>
	</div>
	<div class="tab-pane" id="itemToUpdate">
		<div class="span12 margin-top10 margin-bottom10">
			<div id="jqxgridProductToUpdate" style="width: 100%"></div>
		</div>
	</div>
	<div class="tab-pane" id="detailItems">
		<div class="span12 margin-top10 margin-bottom10">
			<div id="jqxgridInventorySelected" style="width: 100%"></div>
		</div>
	</div>
</div>
