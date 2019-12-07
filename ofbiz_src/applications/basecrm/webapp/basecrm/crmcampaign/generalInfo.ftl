<#if parameters.id?has_content !isThruDate?exists>
<div class="notify-assign" style="z-index: 1">
	<button id="removeCampaign" class="btn form-action-button pull-right"><i class="fa-trash"></i></button>
</div>
</#if>
<div id="notifyUpdateGeneral" class="notify-assign"></div>
<form class="basic-form form-horizontal margin-top10" id="campaignInfo">
	<div class="row-fluid ">
		<div class=" span12 no-left-margin no-widget-header">
			<div class="row-fluid margin-bottom10">
				<div class="span3 text-algin-right">
					<label>${uiLabelMap.CampaignId}</label>
				</div>
				<div class="span9">
					<input autocomplete="off" class="no-space" id="campaignCampaignId"
							data-value="<#if campaign?exists && campaign.marketingCampaignId?exists>${campaign.marketingCampaignId?html}</#if>"/>
				</div>
			</div>
			<div class="row-fluid margin-bottom10">
				<div class="span3 text-algin-right">
					<label class="asterisk">${uiLabelMap.CampaignName}</label>
				</div>
				<div class="span9">
					<input autocomplete="off" class="no-resize no-margin" id="campaignName"/>
				</div>
			</div>
			<div class="row-fluid margin-bottom10">
				<div class="span3 text-algin-right">
					<label>${uiLabelMap.CampaignSummary}</label>
				</div>
				<div class="span9">
					<div autocomplete="off" class="no-resize" name="campaignSummary" id="campaignSummary"></div>
				</div>
			</div>
			<div class="row-fluid margin-bottom10">
				<div class="span3 text-algin-right">
					<label class="asterisk">${uiLabelMap.DmsFromDate}</label>
				</div>
				<div class="span9">
					<div class="row-fluid">
						<div class="span3">
							<div class="row-fluid input-append">
								<div id="fromDate" data-value="<#if campaign?exists && campaign.fromDate?exists>${campaign.fromDate}</#if>"></div>
							</div>
						</div>
						<div class="span9">
							<div class="row-fluid margin-bottom10">
								<div class="span3 text-algin-right">
									<label>${uiLabelMap.DmsThruDate}</label>
								</div>
								<div class="span9">
									<div class="row-fluid input-append">
										<div id="thruDate"
											data-value="<#if campaign?exists && campaign.thruDate?exists>${campaign.thruDate}</#if>"></div>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
			<#if !isThruDate?exists>
			<div class="row-fluid">
				<div class="span3 text-algin-right">
					<label>${uiLabelMap.isActive}</label>
				</div>
				<div class="span9">
					<div class="row-fluid">
						<div class="span3">
							<label class="inline margin-top10" id="isActiveContainer">
								<input id="isActive" type="checkbox" class="ace-switch ace-switch-5"
								<#if campaign?exists && campaign.isActive?exists && campaign.isActive=="Y" &&
									campaign.statusId?exists && campaign.statusId != "MKTG_CAMP_PLANNED">checked</#if>/>
								<span class="lbl"></span> </label>
						</div>
						<div class="span9">
							<div class="row-fluid">
								<div class="span3 text-algin-right">
									<label>${uiLabelMap.isDone}</label>
								</div>
								<div class="span9">
									<label class="inline margin-top10" id="isDoneContainer">
										<input id="isDone" type="checkbox" class="ace-switch ace-switch-5"
										<#if campaign?exists && campaign.statusId?exists && campaign.statusId == "MKTG_CAMP_COMPLETED">checked</#if>/>
										<span class="lbl"></span> </label>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
			<#else>
			<div class="row-fluid margin-bottom10">
				<div class="span3 text-algin-right">
					<label>${uiLabelMap.statusId}</label>
				</div>
				<div class="span9">
					${uiLabelMap.CampaignExpired}
				</div>
			</div>
			</#if>
		</div>
	</div>
</form>
<#if !isThruDate?exists>
<div class="control-action">
	<button id="cancelCampaign" class="btn btn-danger form-action-button pull-right"><i class="fa-remove"></i> ${uiLabelMap.CommonCancel}</button>
	<button id="saveCampaign" class="btn btn-primary form-action-button pull-right"><i class="fa-check"></i> ${uiLabelMap.CommonSave}</button>
</div>
</#if>