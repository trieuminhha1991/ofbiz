<form class="basic-form form-horizontal margin-top10" id="campaignInfo">
	<div class="row-fluid">
		<div class=" span6 no-left-margin no-widget-header">
			<div class="row-fluid margin-bottom10">
				<div class='span5 text-algin-right'>
					<label>${uiLabelMap.CampaignId}</label>
				</div>
				<div class="span7">
					<input autocomplete="off" id="marketingCampaignId"
							value="<#if marketing?exists && marketing.marketingCampaignId?exists>${marketing.marketingCampaignId}</#if>"/>
				</div>
			</div>
			<div class="row-fluid margin-bottom10">
				<div class='span5 text-algin-right'>
					<label class='asterisk'>${uiLabelMap.CampaignName}</label>
				</div>
				<div class="span7">
					<textarea autocomplete="off" class="no-resize"  name="campaignName" id="campaignName" style="width: 76%;"><#if marketing?exists && marketing.campaignName?exists>${StringUtil.wrapString(marketing.campaignName)}</#if></textarea>
				</div>
			</div>
			<div class="row-fluid margin-bottom10">
				<div class='span5 text-algin-right'>
					<label class='asterisk'>${uiLabelMap.marketingTypeId}</label>
				</div>
				<div class="span7">
					<div id="marketingTypeId" data-value="<#if marketing?exists && marketing.marketingTypeId?exists>${marketing.marketingTypeId}</#if>"></div>
				</div>
			</div>
			<div class="row-fluid margin-bottom10">
				<div class='span5 text-algin-right'>
					<label class='asterisk'>${uiLabelMap.dueDate}</label>
				</div>
				<div class="span7">
					<div class="row-fluid input-append">
						<div id="dueDate"
							data-value="<#if marketing?exists && marketing.fromDate?exists>${marketing.fromDate}</#if> - <#if marketing?exists && marketing.thruDate?exists>${marketing.thruDate}</#if>"></div>
					</div>
				</div>
			</div>
		</div>
		<div class=" span6 no-widget-header">
			<div class="row-fluid margin-bottom10">
				<div class='span5 text-algin-right'>
					<label>${uiLabelMap.budgetedCost}</label>
				</div>
				<div class="span7">
					<div id="budgetedCost" data-value="<#if marketing?exists && marketing.budgetedCost?exists>${marketing.budgetedCost}</#if>"></div>
				</div>
			</div>
			<div class="row-fluid margin-bottom10">
				<div class='span5 text-algin-right'>
					<label>${uiLabelMap.estimatedCost}</label>
				</div>
				<div class="span7">
					<div id="estimatedCost" data-value="<#if marketing?exists && marketing.estimatedCost?exists>${marketing.estimatedCost}</#if>"></div>
				</div>
			</div>
			<div class="row-fluid margin-bottom10">
				<div class='span5 text-algin-right'>
					<label>${uiLabelMap.actualCost}</label>
				</div>
				<div class="span7">
					<div id="actualCost" data-value="<#if marketing?exists && marketing.actualCost?exists>${marketing.actualCost}</#if>"></div>
				</div>
			</div>
			<div class="row-fluid margin-bottom10">
				<div class='span5 text-algin-right'>
					<label>${uiLabelMap.DACurrencyUomId}</label>
				</div>
				<div class="span7">
					<div id="currencyUomId" data-value="<#if marketing?exists && marketing.currencyUomId?exists>${marketing.currencyUomId}</#if>"></div>
				</div>
			</div>
			<div class="row-fluid margin-bottom10">
				<div class="span6">
					<div class="row-fluid">
						<div class='span5 text-algin-right'>
							<label>${uiLabelMap.isActive}</label>
						</div>
						<label class="inline margin-top10 margin-left10">
							<input id="isActive" type="checkbox" class="ace-switch ace-switch-5"
							<#if (marketing.isActive)?exists && marketing.isActive == "Y">checked</#if>/>
							<span class="lbl"></span> </label>
					</div>
				</div>
				<div class="span6">
					<div class="row-fluid">
						<div class='span5 text-algin-right'>
							<label>${uiLabelMap.isDone}</label>
						</div>
						<div class="span7">
							<label class="inline margin-top10 margin-left10">
								<input id="isDone" type="checkbox" class="ace-switch ace-switch-5"
								<#if marketing?exists && marketing.statusId?exists && marketing.statusId == 'MKTG_CAMP_COMPLETED'>checked</#if>/>
								<span class="lbl"></span> </label>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
	<div class="row-fluid">
		<div class="span6">
			<div class="row-fluid margin-bottom10">
				<div class='span5 text-algin-right'>
					<label>${uiLabelMap.CampaignSummary}</label>
				</div>
				<div class="span7">
					<textarea autocomplete="off" class="no-resize"  name="campaignSummary" id="campaignSummary"
						 data-value="<#if marketing?exists && marketing.campaignSummary?exists>${StringUtil.wrapString(marketing.campaignSummary)}</#if>"></textarea>
				</div>
			</div>
		</div>
	</div>
</form>
