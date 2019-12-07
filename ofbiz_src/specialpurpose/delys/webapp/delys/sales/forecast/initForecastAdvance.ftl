<div class="row-fluid">
    <div class="span12 widget-container-span">
        <div class="widget-box transparent">
            <div class="widget-header">
                <h4>Sales Forecast</h4>
            </div>
	        <div class="widget-body">
	            <div class="widget-main padding-12 no-padding-left no-padding-right">
	            	<form class="form-horizontal basic-custom-form" id="initForecastAdvance" name="initForecastAdvance" method="post" action="<@ofbizUrl>createForecastAdvance</@ofbizUrl>" style="display: block;">
						<div class="span12">
							<div class="control-group">
								<label class="control-label">${uiLabelMap.DAChooseCustomTimePeriodId}</label>
								<div class="controls">
									<select class="chzn-select" id="customTimePeriodId" name="customTimePeriodId" data-placeholder="${uiLabelMap.DAChooseCustomTimePeriodIdHolder}">
										<#if parameters.customTimePeriodId?exists>
											<#assign customTimePeriodSelected = parameters.customTimePeriodId/>
										</#if>
										<#list listCustomTimePeriod as periodItem>
											<option value="${periodItem.customTimePeriodId}" <#if customTimePeriodSelected?exists && (customTimePeriodSelected == periodItem.customTimePeriodId)>selected="selected"</#if>>${periodItem.periodName} [${periodItem.customTimePeriodId}]</option>
										</#list>
									</select>
									<button type="submit" class="btn btn-mini btn-primary no-bottom-margin margin-left10"><i class="icon-ok open-sans"></i>${uiLabelMap.DACreate} / ${uiLabelMap.DAUpdate}</button>
								</div>
							</div>
						</div><!--.span12-->
					</form>
				</div>
			</div>
		</div>
	</div>
</div>
<script type="text/javascript">
$(function() {
	$(".chzn-select").css('width','220px').chosen({allow_single_deselect:true , no_results_text: "No such state!"})
	.on('change', function(){
		$(this).closest('form').validate().element($(this));
	});
});
</script>