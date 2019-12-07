<div id="OverView-tab" class="tab-pane<#if !activeTab?exists || activeTab == "" || activeTab == "OverView-tab"> active</#if>">
	<div style="position:relative"><!-- class="widget-body"-->
		<div><!--class="widget-main"-->
			<div class="row-fluid" style="margin-top:-12px">
				<div class="form-window-content-custom content-description-left">
					<div class="span6">
						<div class="row-fluid margin-top10">
							<label>${uiLabelMap.ADApplicationId}:</label>
							<div>
								<span>
									${(application.applicationId)?if_exists}
								</span>
							</div>
						</div>
						
						<div class="row-fluid margin-top10">
							<label>${uiLabelMap.ADApplicationName}:</label>
							<div>
								<span>
									${(application.name)?if_exists}
								</span>
							</div>
						</div>
					</div>
					<div class="span6">
						<div class="row-fluid margin-top10">
							<label>${uiLabelMap.ADApplication}:</label>
							<div>
								<span>
									${(application.application)?if_exists}
								</span>
							</div>
						</div>
						
						<div class="row-fluid margin-top10">
							<label>${uiLabelMap.ADPermissionDefault}:</label>
							<div>
								<span>
									${(application.permissionId)?if_exists}
								</span>
							</div>
						</div>
					</div>
					<div class="row-fluid">
						<div class="span12 margin-top10">
							<label>${uiLabelMap.ADDiagram}:</label>
							<div>
								<#include "ModuleDiagram.ftl"/>
							</div>
						</div>
					</div>
				</div><!--.form-window-content-custom-->
			</div><!--.row-fluid-->
		</div>
	</div>
</div>