<#include "component://delys/webapp/delys/marketing/marketingRequestHeader.ftl"/>
<div class="marketing-wrapper">
	<form method="post" id="EditResearchCampaign" class="basic-form form-horizontal" name="EditResearchCampaign" style="margin-bottom: 10px !important;">
		<div class="row-fluid">
			<div class=" span6 no-left-margin no-widget-header">
				<div class="control-group no-left-margin ">
					<label for="campaignName" class="asterisk">${uiLabelMap.ResearchCampaignName}</label>
					<div class="controls">
						<textarea autocomplete="off"  placeholder="${uiLabelMap.CampaignName}" class='no-margin-top no-resize' 
						name="campaignName" cols="60" rows="3" id="campaignName"></textarea>
					</div>
				</div>
				<div class="control-group no-left-margin ">
					<label>${uiLabelMap.ResearchCampaignSummary}</label>
					<div class="controls">
						<textarea autocomplete="off"  placeholder="${uiLabelMap.CampaignSummary}" class='no-margin-top no-resize' 
						name="campaignSummary" cols="60" rows="3" id="campaignSummary"></textarea>
					</div>
				</div>
				<div class="control-group no-left-margin ">
					<label for="place" class="asterisk">${uiLabelMap.Place}</label>
					<div class="controls">
						<div id="place"></div>
						<script>var places=[<#if province?has_content><#list province as pr>{geoId: "${pr.geoId}", geoName: "${pr.geoName}"},</#list></#if>]</script>
					</div>
				</div>	
				<div class='space-8'></div>
				<div class="control-group no-left-margin ">
					<label class="asterisk">${uiLabelMap.MarketingProduct}</label>
					<div class="controls">
						<select id="productId" multiple="" autocomplete="off" data-placeholder="${uiLabelMap.chooseProductPh}">
							<#if products?has_content>
							<#list products as product>
							<option value="${product.productId}">${product.productName}</option>
							</#list>
							</#if>
						</select>
					</div>
				</div>

			</div>
			<div class=" span6 no-widget-header">
				<div class="control-group control-group2  no-left-margin ">
					<label for="campaignName">${uiLabelMap.ResearchType}</label>
					<div class="controls">
						<select autocomplete="off">
							<option value="RESEARCH_FREQ">${uiLabelMap.RESEARCH_FREQ}</option>
							<option value="RESEARCH_UNED">${uiLabelMap.RESEARCH_UNED}</option>
						</select>
					</div>
				</div>
				<!-- <div class="control-group no-left-margin ">
					<label>Budgeted Cost</label>
					<div class="controls">
						<input autocomplete="off" name="budgetedCost" size="6" id="budgetedCost" type="text">
					</div>
				</div> -->
				<div class="control-group control-group2  no-left-margin ">
					<label>Estimated cost</label>
					<div class="controls">
						<!-- <input autocomplete="off" name="estimatedCost" size="6" id="estimatedCost" type="text" placeholder="${uiLabelMap.estimatedCostEx}"> -->
						<div id="estimatedCost"></div>
					</div>
				</div>

				<div class="control-group control-group2  no-left-margin ">
					<label>${uiLabelMap.MarketingHumanResource}</label>
					<div class="controls">
						<input autocomplete="off" name="people" size="55" placeholder="${uiLabelMap.MarketingHumanResourceEx}" id="people" type="text"/>
					</div>
				</div>
				<div class="control-group control-group2  no-left-margin">
					<label class="asterisk">${uiLabelMap.dueDate}</label>
					<div class="controls">
						<div class="row-fluid input-append">
							<input autocomplete="off" class="date-picker no-margin-bottom" id="fromDate" style='width: 179px;' type="text" placeholder="${uiLabelMap.dueDateEx}"/>
							<span class="add-on"> <i class="icon-calendar"></i> </span>
						</div>
					</div>
				</div>
				<div class="control-group control-group2  no-left-margin ">
					<label>${uiLabelMap.isActive}</label>
					<div class="controls">
						<select name="isActive" id="isActive" size="null">
							<option value="N">N</option>
							<option value="Y">Y</option>
						</select>
					</div>
				</div>
			</div>
		</div>
	</form>
	<#include "component://delys/webapp/delys/marketing/costList.ftl"/>
	<div class="control-action">
		<button class="btn btn-primary" id="submit">
			<i class='fa fa-check'></i>&nbsp;${uiLabelMap.submit}
		</button>
		<button class="btn btn-success" id="reset">
			<i class='fa fa-refresh'></i>&nbsp;${uiLabelMap.reset}
		</button>
	</div>
	<scipt>
		
	</scipt>
</div>