<div class="widget-box widget-box-olbius">
	<div class="widget-header header-color-olbius">
		<h5 class="bigger lighter">
			<i class="fa fa-info"></i>&nbsp;
			${uiLabelMap.generalInfo}
		</h5>
	</div>

	<div class="widget-body">
		<div class="widget-main no-padding">
			<form class="basic-form form-horizontal">
				<div class="row-fluid">
					<div class=" span6 no-left-margin no-widget-header">
						<div class="control-group no-left-margin ">
							<label for="campaignName">${uiLabelMap.CampaignName}</label>
							<div class="controls">
								<textarea autocomplete="off" <#if isDisable?exists>disabled</#if> placeholder="${uiLabelMap.CampaignName}" style="margin-top: 0; resize: none"  name="campaignName" id="campaignName"><#if info?exists && info.campaignName?exists>${info.campaignName}</#if></textarea>
							</div>
						</div>
						<div class="control-group no-left-margin ">
							<label>${uiLabelMap.CampaignSummary}</label>
							<div class="controls">
								<textarea autocomplete="off" <#if isDisable?exists>disabled</#if>  placeholder="${uiLabelMap.CampaignSummary}" style="margin-top: 0; resize: none" name="campaignSummary" cols="60" rows="3" id="campaignSummary"><#if info?exists && info.campaignSummary?exists>${info.campaignSummary}</#if></textarea>
								<i class="more-edit"></i>
							</div>
						</div>
						<div class="control-group no-left-margin ">
							<label>${uiLabelMap.MarketingHumanResource}</label>
							<div class="controls">
								<input autocomplete="off" <#if isDisable?exists>disabled</#if>  placeholder="${uiLabelMap.MarketingHumanResourceEx}" name="people" size="55" id="people" type="number" value="<#if info?exists && info.people?exists>${info.people}</#if>"/>
							</div>
						</div>
					</div>
					<div class=" span6 no-widget-header">
						<div class="control-group no-left-margin ">
							<label>${uiLabelMap.dueDate}</label>
							<div class="controls">
								<div class="row-fluid input-append">
									<input autocomplete="off" <#if isDisable?exists>disabled</#if>  placeholder="${uiLabelMap.dueDateEx}" class="span10 date-picker no-margin" id="fromDate" type="text" />
									<span class="add-on"> <i class="icon-calendar"></i> </span>
								</div>
							</div>
						</div>
						<div class="control-group no-left-margin ">
							<label>${uiLabelMap.isActive}</label>
							<div class="controls">
								<select name="isActive" id="isActive" size="null" value="<#if info?exists && info.isActive?exists>${info.isActive}</#if>" <#if isDisable?exists>disabled</#if> >
									<option value="N">N</option>
									<option value="Y">Y</option>
								</select>
							</div>
						</div>
						<div class="control-group no-left-margin ">
							<label>${uiLabelMap.estimatedCost}</label>
							<div class="controls">
								<input autocomplete="off" <#if isDisable?exists>disabled</#if>  placeholder="${uiLabelMap.estimatedCostEx}" name="estimatedCost" size="6" id="estimatedCost" type="text" value="<#if info?exists && info.estimatedCost?exists>${info.estimatedCost}</#if>">
							</div>
						</div>
					</div>
				</div>
			</form>	
		</div>
	</div>
</div>