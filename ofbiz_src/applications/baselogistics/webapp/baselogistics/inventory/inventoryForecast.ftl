<@jqGridMinimumLib />
<#include "script/inventoryForecastScript.ftl"/>
<h4 class="row header smaller lighter blue" style="margin-left: 10px !important;font-weight:500;line-height:20px;font-size:18px;">
	${uiLabelMap.InventoryForecast}
</h4>
<div class="rowfluid" id="formInfo">
	<div class="form-horizontal form-window-content-custom label-text-left content-description" style="margin:10px">
		<div class="span12">
			<div class='row-fluid'>	
				<div class='span6'>
					<div class='row-fluid'>
						<div class='span5' style="text-align: right">
							<span class="title asterisk">${uiLabelMap.FromDate}</span>
						</div>
						<div class='span7'>
							<div id="fromDate" style="width: 100%;"></div>
						</div>
					</div>
					<div class='row-fluid margin-top5'>
						<div class='span5' style="text-align: right">
							<span class="title asterisk">${uiLabelMap.ThruDate}</span>
						</div>
						<div class='span7'>
							<div id="thruDate" style="width: 100%;"></div>
						</div>
					</div>
					<div class='row-fluid margin-top5'>
						<div class='span5' style="text-align: right">
							<span class="title">${uiLabelMap.Facility}</span>
						</div>
						<div class='span7'>
							<div id="facilityId" style="width: 100%;"></div>
						</div>
					</div>
					<div class='row-fluid margin-top5'>
						<div class='span5' style="text-align: right">
							<span class="title">${uiLabelMap.IncludeExpired}</span>
						</div>
						<div class='span7'>
							<div id='includeExpiredId' style='float: left; margin-left: -2px !important;'></div>
						</div>
					</div>
					<div class="margin-top10">
						<div class='span5' style="text-align: right">
							
						</div>
						<div class='span6'>
							<button id="okButton" class='btn btn-primary form-action-button pull-right' style="margin-right: 24px !important;"><i class='fa-check'></i> ${uiLabelMap.OK}</button>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
	<div class="row-fluid">
	<div class="span12">
		<h4 class="row header smaller lighter blue" style="font-weight:500;line-height:20px;font-size:18px;">
		    ${uiLabelMap.ListProduct}
		</h4>
		<div id = "jqxgridInventoryForecast"></div>
	</div>
</div>
</div>
