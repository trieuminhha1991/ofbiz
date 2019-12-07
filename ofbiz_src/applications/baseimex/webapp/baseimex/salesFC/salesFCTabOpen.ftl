<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>
<script type="text/javascript" src="/aceadmin/assets/js/bootbox.min.js"></script>
<#assign salesForecastId = parameters.salesForecastId />
<div class="row-fluid">
	<div class="span12">
		<div class="widget-box transparent" id="recent-box">
			<div class="widget-header" style="border-bottom:none">
				<div style="width:100%; border-bottom: 1px solid #c5d0dc"><#--widget-toolbar no-border-->
					<div class="row-fluid">
					<div class="span10">
						<div class="tabbable">
								<ul class="nav nav-tabs" id="recent-tab"><#-- nav-tabs-menu-->
									<li<#if !activeTab?exists || activeTab == "" || activeTab == "salesFCMonth-tab"> class="active"</#if>>
										<a href="viewSalesForecastDetailByPO?salesForecastId=${salesForecastId}">${uiLabelMap.DmsSalesFCMonth}</a>
									</li>
									<li<#if activeTab?exists && activeTab == "salesFCWeek-tab"> class="active"</#if>>
										<a href="viewSalesForecastWeekByPO?salesForecastId=${salesForecastId}">${uiLabelMap.DmsSalesFCWeek}</a>
									</li>
								</ul>
							</div><!--.tabbable-->
						</div>
					</div>
					<script type="text/javascript">
						$('[data-rel=tooltip]').tooltip();
					</script>
					<style type="text/css">
						.button-action {
							font-size:18px; padding:0 0 0 8px;
						}
					</style>
				</div>
			</div>
		</div>
	</div>
</div>
			
					
<script>
	
</script>
					